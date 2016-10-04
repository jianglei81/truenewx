package org.truenewx.web.rpc.server;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.core.annotation.Caption;
import org.truenewx.core.enums.support.EnumDictResolver;
import org.truenewx.core.enums.support.EnumItem;
import org.truenewx.core.enums.support.EnumType;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.NetUtil;
import org.truenewx.core.util.PropertyMeta;
import org.truenewx.data.rpc.annotation.RpcProperty;
import org.truenewx.web.authority.validator.AuthorityValidator;
import org.truenewx.web.exception.annotation.HandleableExceptionMessage;
import org.truenewx.web.login.LoginPredicate;
import org.truenewx.web.menu.MenuResolver;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.rpc.serializer.RpcSerializer;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;
import org.truenewx.web.rpc.server.meta.RpcControllerMeta;
import org.truenewx.web.rpc.server.meta.RpcTypeMeta;
import org.truenewx.web.rpc.server.meta.RpcVariableMeta;
import org.truenewx.web.rpc.util.RpcUtil;
import org.truenewx.web.spring.context.SpringWebContext;
import org.truenewx.web.util.WebUtil;

/**
 * 基于Spring MVC的RPC调用代理控制器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Controller("rpcServerController")
@RequestMapping("/rpc")
public class RpcServerController implements RpcServerInterceptor {

    private RpcServer server;
    @Autowired
    private RpcSerializer serializer;
    @Autowired(required = false)
    @Qualifier("loginPredicate")
    private LoginPredicate loginPredicate;

    @Autowired(required = false)
    private AuthorityValidator authorityValidator;

    @Autowired
    private ApplicationContext context;

    /**
     * 注入菜单解析器
     */
    private Menu menu;
    @Autowired
    private EnumDictResolver enumDictResolver;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param menuResolver
     *            菜单解析器
     */
    @Autowired(required = false)
    public void setMenuResolver(final MenuResolver menuResolver) {
        this.menu = menuResolver.getFullMenu();
    }

    @Autowired
    public void setInvoker(final RpcServerInvoker invoker) {
        invoker.setInterceptor(this);
        this.server = invoker;
    }

    @Override
    public void beforeMethods(final Object bean) throws Exception {
    }

    @Override
    public void beforeInvoke(final String beanId, final Method method,
                    final HttpServletRequest request, final HttpServletResponse response)
                    throws Exception {
        final RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
        // 检查局域网限制
        final String ip = WebUtil.getRemoteAddrIp(request);
        if (rpcMethod.lan() && !NetUtil.isLanIp(ip)) {
            this.logger.warn("Forbidden rpc request {}.{} from {}", beanId, method.getName(), ip);
            response.sendError(HttpStatus.FORBIDDEN.value()); // 禁止非局域网访问
            return;
        }
        // 检查登录限制
        if (this.loginPredicate != null) {
            if (!this.loginPredicate.isLogined(request, response) && rpcMethod.logined()) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            if (rpcMethod.logined() && this.authorityValidator != null) { // 登录校验RPC权限
                final HandlerMethod handler = new HandlerMethod(this.context.getBean(beanId),
                                method);
                // 先校验@RpcAuth注解中限定的权限
                String validatedAuthority = RpcUtil.getAuthority(rpcMethod);
                this.authorityValidator.validate(request, response, handler, validatedAuthority);
                // 再校验菜单配置中限定的权限
                if (this.menu != null) {
                    validatedAuthority = this.menu.getAuth(beanId, method.getName(),
                                    method.getParameterTypes().length);
                    this.authorityValidator.validate(request, response, handler,
                                    validatedAuthority);
                }
            }
        }
    }

    @RequestMapping(value = "/methods/{beanId}", method = RequestMethod.GET)
    @HandleableExceptionMessage
    @ResponseBody
    public String methods(@PathVariable("beanId") final String beanId) throws Exception {
        final Collection<String> methodNames = this.server.methods(beanId);
        return this.serializer.serializeCollection(methodNames);
    }

    @RequestMapping(value = "/invoke/{beanId}/{methodName}", method = { RequestMethod.POST,
                    RequestMethod.GET })
    @HandleableExceptionMessage
    @ResponseBody
    public String invoke(@PathVariable("beanId") final String beanId,
                    @PathVariable("methodName") final String methodName,
                    @RequestParam(value = "args", required = false) final String argString,
                    final HttpServletRequest request, final HttpServletResponse response)
                    throws Throwable {
        response.setHeader("Timestamp-Before", String.valueOf(System.currentTimeMillis()));
        final RpcInvokeResult result = this.server.invoke(beanId, methodName, argString, request,
                        response);
        response.setHeader("Timestamp-After", String.valueOf(System.currentTimeMillis()));
        return this.serializer.serializeBean(result.getValue(), result.getFilters());
    }

    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public ModelAndView api(final HttpServletRequest request, final HttpServletResponse response)
                    throws IOException {
        final ModelAndView mav = new ModelAndView("rpc/api");
        if (NetUtil.isLanIp(WebUtil.getRemoteAddrIp(request))) {
            final Map<String, Object> beans = this.context
                            .getBeansWithAnnotation(RpcController.class);
            final Map<String, List<RpcControllerMeta>> controllerMap = new TreeMap<>();
            for (final Entry<String, Object> entry : beans.entrySet()) {
                final String beanId = entry.getKey();
                final Object bean = entry.getValue();
                final RpcControllerMeta controllerMeta = this.server.getMeta(beanId, bean);
                final String module = controllerMeta.getModule();
                if (module != null) {
                    List<RpcControllerMeta> controllerMetas = controllerMap.get(module);
                    if (controllerMetas == null) {
                        controllerMetas = new ArrayList<>();
                        controllerMap.put(module, controllerMetas);
                    }
                    controllerMetas.add(controllerMeta);
                    Collections.sort(controllerMetas); // 确保元数据的顺序
                }
            }
            mav.addObject("controllerMap", controllerMap);
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value()); // 禁止非局域网访问
        }
        return mav;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/properties/arg/{beanId}/{methodName}/{argCount}/{argIndex}")
    @ResponseBody
    public String argProperties(@PathVariable("beanId") final String beanId,
                    @PathVariable("methodName") final String methodName,
                    @PathVariable("argCount") final int argCount,
                    @PathVariable("argIndex") final int argIndex, final HttpServletRequest request,
                    final HttpServletResponse response) throws IOException {
        if (NetUtil.isLanIp(WebUtil.getRemoteAddrIp(request))) {
            final RpcVariableMeta argMeta = this.server.getArgMeta(beanId, methodName, argCount,
                            argIndex);
            if (argMeta == null) {
                return "null";
            }
            final RpcTypeMeta argTypeMeta = argMeta.getType();
            final Class<?> clazz = argTypeMeta.getType();

            final List<RpcVariableMeta> metas;
            if (clazz.isEnum()) {
                final Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
                final String subType = this.server.getEnumSubType(beanId, methodName, argCount,
                                enumClass);
                metas = getConstantMetas(enumClass, subType);
            } else {
                final Collection<PropertyMeta> propertyMetas = ClassUtil.findPropertyMetas(clazz,
                                false, true, true, argTypeMeta.getIncludes(),
                                argTypeMeta.getExcludes());
                metas = getPropertyVariableMetas(clazz, propertyMetas, false);
            }
            return this.serializer.serializeBean(metas);
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value()); // 禁止非局域网访问
            return null;
        }
    }

    private List<RpcVariableMeta> getConstantMetas(final Class<? extends Enum<?>> enumClass,
                    final String subType) {
        final EnumType enumType = this.enumDictResolver.getEnumType(enumClass.getName(), subType,
                        SpringWebContext.getLocale());
        if (enumType != null) {
            final List<RpcVariableMeta> metas = new ArrayList<>();
            for (final Enum<?> constant : enumClass.getEnumConstants()) {
                final String name = constant.name();
                final RpcVariableMeta meta = new RpcVariableMeta(enumClass);
                meta.setName(name);
                final EnumItem item = enumType.getItem(name);
                if (item != null) {
                    meta.setCaption(item.getCaption());
                }
                metas.add(meta);
            }
            return metas;
        }
        return null;
    }

    private List<RpcVariableMeta> getPropertyVariableMetas(final Class<?> clazz,
                    final Collection<PropertyMeta> propertyMetas, final boolean getter) {
        final List<RpcVariableMeta> metas = new ArrayList<>();
        for (final PropertyMeta propertyMeta : propertyMetas) {
            final String propertyName = propertyMeta.getName();
            final Class<?> propertyType = propertyMeta.getType();
            final RpcVariableMeta meta = new RpcVariableMeta(propertyType);
            meta.setName(propertyName);
            // 处理属性注解
            for (final Annotation annotation : propertyMeta.getAnnotations()) {
                if (annotation instanceof RpcProperty) {
                    final RpcProperty rpcProperty = (RpcProperty) annotation;
                    meta.setCaption(rpcProperty.caption()); // 优先使用@RpcProperty中的caption
                    meta.getType().setComponentType(rpcProperty.componentType());
                } else if (annotation instanceof Caption) {
                    if (StringUtils.isBlank(meta.getCaption())) { // 其次使用@Caption中的caption
                        final Caption caption = (Caption) annotation;
                        meta.setCaption(caption.value());
                    }
                } else if (annotation instanceof Deprecated) { // 不再推荐使用标记
                    meta.setDeprecated(true);
                }
            }
            metas.add(meta);
        }
        return metas;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/properties/result/{beanId}/{methodName}/{argCount}/{className}")
    @ResponseBody
    public String resultProperties(@PathVariable("beanId") final String beanId,
                    @PathVariable("methodName") final String methodName,
                    @PathVariable("argCount") final int argCount,
                    @PathVariable("className") final String className,
                    final HttpServletRequest request, final HttpServletResponse response)
                    throws IOException {
        if (NetUtil.isLanIp(WebUtil.getRemoteAddrIp(request))) {
            Class<?> clazz;
            try {
                clazz = this.context.getClassLoader().loadClass(className);
            } catch (final ClassNotFoundException e) {
                return "null";
            }

            List<RpcVariableMeta> metas;
            if (clazz.isEnum()) {
                final Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
                final String subType = this.server.getEnumSubType(beanId, methodName, argCount,
                                enumClass);
                metas = getConstantMetas(enumClass, subType);
            } else {
                final RpcResultFilter filter = this.server.getResultFilter(beanId, methodName,
                                argCount, clazz);
                final String[] includes = filter == null ? null : filter.includes();
                final String[] excludues = filter == null ? null : filter.excludes();
                final Collection<PropertyMeta> propertyMetas = ClassUtil.findPropertyMetas(clazz,
                                true, false, true, includes, excludues);
                metas = getPropertyVariableMetas(clazz, propertyMetas, true);
            }
            return this.serializer.serializeBean(metas);
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value()); // 禁止非局域网访问
            return null;
        }
    }

}
