package org.truenewx.web.rpc.server;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.serializer.StringSerializer;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.core.util.ArrayUtil;
import org.truenewx.core.util.BeanUtil;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.CollectionUtil;
import org.truenewx.core.util.NetUtil;
import org.truenewx.web.menu.MenuResolver;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.rpc.server.annotation.RpcArg;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcEnum;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.rpc.server.annotation.RpcResult;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;
import org.truenewx.web.rpc.server.functor.PredEquivalentClass;
import org.truenewx.web.rpc.server.meta.RpcControllerMeta;
import org.truenewx.web.rpc.server.meta.RpcMethodMeta;
import org.truenewx.web.rpc.server.meta.RpcVariableMeta;
import org.truenewx.web.security.annotation.Accessibility;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.mgt.SubjectManager;
import org.truenewx.web.security.subject.Subject;
import org.truenewx.web.spring.context.SpringWebContext;
import org.truenewx.web.spring.util.SpringWebUtil;
import org.truenewx.web.util.WebUtil;

import com.google.common.base.Defaults;

/**
 * RPC服务端调用器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class RpcServerInvoker implements RpcServer, ApplicationContextAware {
    private ApplicationContext context;
    @Autowired
    private StringSerializer serializer;
    private Map<String, RpcControllerMeta> metaMap = new HashMap<>();
    private Menu menu;
    @Autowired(required = false)
    private SubjectManager subjectManager;
    @Autowired(required = false)
    private RpcInvokeInterceptor interceptor;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    private ApplicationContext getContext() {
        if (this.context.getParent() == null) { // 若为根上下文，尝试从WEB容器中获取下级上下文
            HttpServletRequest request = SpringWebContext.getRequest();
            if (request != null) {
                ApplicationContext context = SpringWebUtil.getApplicationContext(request);
                if (context != null) {
                    this.context = context;
                }
            }
        }
        return this.context;
    }

    @Autowired(required = false)
    public void setMenuResolver(MenuResolver menuResolver) {
        this.menu = menuResolver.getFullMenu();
    }

    private RpcInvokeInterceptor getInterceptor() {
        if (this.interceptor == null) {
            this.interceptor = SpringUtil.getFirstBeanByClass(this.context,
                    RpcInvokeInterceptor.class);
        }
        return this.interceptor;
    }

    private RpcControllerMeta getMeta(String beanId) {
        RpcControllerMeta meta = this.metaMap.get(beanId);
        if (meta == null) {
            Object bean = getContext().getBean(beanId);
            meta = buildMeta(beanId, bean);
        }
        return meta;
    }

    private RpcControllerMeta buildMeta(String beanId, Object bean) {
        if (bean.getClass().getAnnotation(RpcController.class) == null) {
            throw new IllegalArgumentException(
                    "The '" + beanId + "' is not an " + RpcController.class.getSimpleName());
        }
        RpcControllerMeta meta = new RpcControllerMeta(beanId, bean);
        this.metaMap.put(beanId, meta);
        return meta;
    }

    @Override
    public Collection<String> methods(String beanId) throws Exception {
        return getMeta(beanId).getMethodNames();
    }

    /**
     * 执行指定RPC类的指定方法
     *
     * @param beanId
     *            类名
     * @param methodName
     *            方法名
     * @param args
     *            参数集
     * @return 序列化后的字符串形式的执行结果
     * @throws Exception
     *             如果执行过程中出现错误
     */
    @Override
    public RpcInvokeResult invoke(String beanId, String methodName, String argString,
            HttpServletRequest request, HttpServletResponse response) throws Throwable {
        // 参数转换
        RpcControllerMeta meta = getMeta(beanId);
        Method method;
        Object[] args;
        if (argString == null) { // Map形式的参数集
            method = meta.getMethod(methodName, null);
            Class<?>[] declaredArgTypes = method.getParameterTypes();
            RpcArg[] rpcArgs = method.getAnnotation(RpcMethod.class).args();
            args = new Object[declaredArgTypes.length];
            for (int i = 0; i < declaredArgTypes.length; i++) {
                Class<?> argType = declaredArgTypes[i];
                RpcArg rpcArg = ArrayUtil.get(rpcArgs, i);
                String argValueString = null;
                if (rpcArg != null) {
                    String parameterName = rpcArg.name();
                    if (StringUtils.isNotBlank(parameterName)) {
                        argValueString = request.getParameter(parameterName);
                    }
                }
                if (argValueString == null) { // 没有参数则尝试取参数类型的默认值
                    if (argType.isPrimitive()) { // 原生类型才取默认值
                        args[i] = Defaults.defaultValue(argType);
                    }
                } else { // 有参数则按照参数类型反序列化
                    args[i] = deserializeArgValue(argValueString, argType, rpcArg);
                }
            }
        } else { // 数组形式的参数集
            args = this.serializer.deserializeArray(argString);
            method = meta.getMethod(methodName, args.length);
            Class<?>[] declaredArgTypes = method.getParameterTypes(); // 声明参数类型集
            RpcArg[] rpcArgs = method.getAnnotation(RpcMethod.class).args();
            for (int i = 0; i < declaredArgTypes.length; i++) {
                Class<?> argType = declaredArgTypes[i];
                // 集合或数组类型参数需重新按照元素类型反序列化
                RpcArg rpcArg = ArrayUtil.get(rpcArgs, i);
                if ((Collection.class.isAssignableFrom(argType) && rpcArg != null
                        && rpcArg.componentType() != Object.class)
                        || (argType.isArray() && ClassUtil.isComplex(argType.getComponentType()))) {
                    String argValueString = this.serializer.serialize(args[i]);
                    args[i] = deserializeArgValue(argValueString, argType, rpcArg);
                }
            }
            Class<?>[] argTypes = getArgTypes(args); // 实际参数类型集
            // 此时参数个数必然相等，但参数类型可能不等价，需要进行参数转换
            for (int i = 0; i < argTypes.length; i++) {
                // 参数类型不等价，则该参数需要转换
                if (args[i] != null // 参数值为null的不需要转换
                        && !PredEquivalentClass.INSTANCE.apply(declaredArgTypes[i], argTypes[i])) {
                    transferArgValue(method, declaredArgTypes, args, i);
                }
            }
        }

        // 执行调用
        try {
            if (!validate(beanId, method, request, response)) {
                return new RpcInvokeResult(Strings.EMPTY);
            }
            if (getInterceptor() != null) {
                this.interceptor.beforeInvoke(beanId, method, args);
            }
            Object result = method.invoke(meta.getController(), args);
            if (this.interceptor != null) {
                this.interceptor.afterInvoke(beanId, method, args, result);
            }
            RpcResult rpcResult = method.getAnnotation(RpcMethod.class).result();
            RpcResultFilter[] resultFilters = rpcResult.filter();
            return new RpcInvokeResult(result, resultFilters);
        } catch (HandleableException e) {
            throw e;
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                this.logger.error(cause.getMessage(), cause);
                throw cause;
            }
            throw e;
        }
    }

    private boolean validate(String beanId, Method method, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String methodName = method.getName();
        // 校验Accessibility注解限制
        Accessibility accessibility = method.getAnnotation(Accessibility.class);
        if (accessibility != null) {
            // 检查局域网限制
            if (accessibility.lan()) {
                String ip = WebUtil.getRemoteAddrIp(request);
                if (!NetUtil.isLanIp(ip)) {
                    this.logger.warn("Forbidden rpc request {}.{} from {}", beanId, methodName, ip);
                    response.sendError(HttpStatus.FORBIDDEN.value()); // 禁止非局域网访问
                    return false;
                }
            }
            // 在访问性注解中设置了可匿名访问，则验证通过
            if (accessibility.anonymous()) {
                return true;
            }
        }
        // 校验菜单权限
        int argCount = method.getParameterCount();
        // 在菜单中设置了可匿名访问，则验证通过
        if (this.menu != null && this.menu.isAnonymous(beanId, methodName, argCount)) {
            return true;
        }
        if (this.subjectManager != null) {
            Subject subject = this.subjectManager.getSubject(request, response);
            if (subject != null) {
                if (!subject.isLogined()) { // 未登录
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return false;
                }
                if (this.menu != null) { // 已登录，校验菜单配置中限定的权限
                    Authority authority = this.menu.getAuthority(beanId, methodName, argCount);
                    // 此时授权可能为null，为null时将被视为无访问权限，意味着在配置有菜单的系统中，RPC请求均应在菜单配置中进行配置
                    subject.validateAuthority(authority);
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void transferArgValue(Method method, Class<?>[] declaredArgTypes, Object[] args, int i)
            throws InstantiationException, IllegalAccessException {
        Class<?> declaredArgType = declaredArgTypes[i];
        Object arg = args[i];
        Class<?> actualArgType = arg.getClass();
        if (Collection.class.isAssignableFrom(declaredArgType)) { // 声明为集合
            if (actualArgType.isArray()) { // 实际为数组
                Collection<?> newCollection = (Collection<?>) declaredArgType.newInstance();
                Object array = arg;
                CollectionUtils.mergeArrayIntoCollection(array, newCollection);
                args[i] = newCollection;
            } else if (Collection.class.isAssignableFrom(actualArgType)) { // 实际也为集合
                RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
                RpcArg rpcArg = ArrayUtil.get(rpcMethod.args(), i);
                if (rpcArg != null) {
                    Class<?> componentType = rpcArg.componentType();
                    if (componentType != Object.class) {
                        @SuppressWarnings("rawtypes")
                        Collection newCollection = (Collection) actualArgType.newInstance();
                        for (Object obj : (Collection<?>) arg) {
                            if (obj instanceof Map) { // 元素为Map才可转换
                                Map<String, Object> map = (Map<String, Object>) obj;
                                Object bean = BeanUtil.toBean(map, componentType);
                                newCollection.add(bean);
                            } else {
                                newCollection.add(obj);
                            }
                        }
                        args[i] = newCollection;
                    }
                }
            }
        } else if (Collection.class.isAssignableFrom(actualArgType) && declaredArgType.isArray()) { // 声明为数组，实际为集合
            Collection<?> collection = (Collection<?>) arg;
            Class<?> componentType = declaredArgType.getComponentType();
            args[i] = Array.newInstance(componentType, collection.size());
            int j = 0;
            for (Object value : collection) {
                if (value instanceof Map && !Map.class.isAssignableFrom(componentType)
                        && !componentType.isPrimitive()) { // 实际值为Map，期望值为复合对象
                    value = BeanUtil.toBean((Map<String, Object>) value, componentType);
                }
                Array.set(args[i], j++, value);
            }
        } else if (Map.class.isAssignableFrom(declaredArgType)
                && Map.class.isAssignableFrom(actualArgType)) { // 声明和实际均为Map
            RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            RpcArg rpcArg = ArrayUtil.get(rpcMethod.args(), i);
            if (rpcArg != null) {
                Class<?> componentType = rpcArg.componentType();
                if (componentType != Object.class) {
                    Map<String, Object> map = (Map<String, Object>) arg;
                    for (Entry<String, Object> entry : map.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof Map) { // Map的值为Map才可以转换
                            value = BeanUtil.toBean((Map<String, Object>) value, componentType);
                            entry.setValue(value);
                        }
                    }
                }
            }
        } else { // 其它需要转换的情况，均先序列号成字符串再反序列化为指定声明类型
            args[i] = this.serializer.deserialize(this.serializer.serialize(arg), declaredArgType);
        }
    }

    private Object deserializeArgValue(String argValueString, Class<?> argType, RpcArg rpcArg) {
        if (Collection.class.isAssignableFrom(argType)) { // 声明参数类型为集合，则按指定元素类型反序列化
            Class<?> elementType = rpcArg != null ? rpcArg.componentType() : Object.class;
            return this.serializer.deserializeList(argValueString, elementType);
        } else if (argType.isArray()) { // 声明参数类型为数组，则按数组元素类型反序列化
            Object[] array = this.serializer.deserializeArray(argValueString);
            Class<?> elementType = argType.getComponentType();
            if (elementType != Object.class && ClassUtil.isComplex(elementType)) { // 数组元素为复合类型，则需要转换为复合类型
                Class<?>[] elementTypes = new Class<?>[array.length];
                for (int j = 0; j < array.length; j++) {
                    elementTypes[j] = elementType;
                }
                array = this.serializer.deserializeArray(argValueString, elementTypes);
            }
            return array;
        } else { // 默认按声明参数类型反序列化
            return this.serializer.deserialize(argValueString, argType);
        }
    }

    private Class<?>[] getArgTypes(Object[] args) {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            types[i] = arg == null ? null : arg.getClass();
        }
        return types;
    }

    @Override
    public RpcControllerMeta getMeta(String beanId, Object bean) {
        RpcControllerMeta meta = this.metaMap.get(beanId);
        if (meta == null) {
            meta = buildMeta(beanId, bean);
        }
        return meta;
    }

    @Override
    public RpcVariableMeta getArgMeta(String beanId, String methodName, int argCount,
            int argIndex) {
        RpcControllerMeta meta = getMeta(beanId);
        try {
            RpcMethodMeta methodMeta = meta.getMethodMeta(methodName, argCount);
            if (methodMeta != null) {
                return CollectionUtil.get(methodMeta.getArgMetas(), argIndex);
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public String getEnumSubType(String beanId, String methodName, int argCount,
            Class<? extends Enum<?>> enumClass) {
        RpcControllerMeta meta = getMeta(beanId);
        try {
            Method method = meta.getMethod(methodName, argCount);
            RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            if (rpcMethod != null) {
                for (RpcEnum rpcEnum : rpcMethod.enums()) {
                    if (rpcEnum.type().equals(enumClass)) {
                        return rpcEnum.sub();
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public RpcResultFilter getResultFilter(String beanId, String methodName, int argCount,
            Class<?> resultType) {
        RpcControllerMeta meta = getMeta(beanId);
        try {
            Method method = meta.getMethod(methodName, argCount);
            RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            if (rpcMethod != null) {
                RpcResultFilter[] filters = rpcMethod.result().filter();
                for (RpcResultFilter filter : filters) {
                    if (filter.type() == resultType) {
                        return filter;
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}
