package org.truenewx.web.rpc.server;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.core.enums.support.EnumDictResolver;
import org.truenewx.core.enums.support.EnumItem;
import org.truenewx.core.enums.support.EnumType;
import org.truenewx.core.i18n.PropertyCaptionResolver;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.core.tuple.Binary;
import org.truenewx.core.tuple.Binate;
import org.truenewx.core.util.CaptionUtil;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.MathUtil;
import org.truenewx.core.util.NetUtil;
import org.truenewx.core.util.PropertyMeta;
import org.truenewx.data.annotation.ComponentType;
import org.truenewx.data.model.Model;
import org.truenewx.data.validation.config.annotation.ValidationAlias;
import org.truenewx.web.rpc.RpcPort;
import org.truenewx.web.rpc.serializer.RpcSerializer;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.rpc.server.annotation.RpcResult;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;
import org.truenewx.web.rpc.server.meta.NoSuchRpcMethodException;
import org.truenewx.web.rpc.server.meta.RpcControllerMeta;
import org.truenewx.web.rpc.server.meta.RpcMethodMeta;
import org.truenewx.web.rpc.server.meta.RpcTypeMeta;
import org.truenewx.web.rpc.server.meta.RpcVariableMeta;
import org.truenewx.web.spring.context.SpringWebContext;
import org.truenewx.web.spring.util.SpringWebUtil;
import org.truenewx.web.util.WebUtil;
import org.truenewx.web.validation.generate.ModelValidationGenerator;

/**
 * RPC接口文档生成控制器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@RpcController
@RequestMapping("/rpc/api")
public class RpcApiController extends RpcControllerSupport {

    @Autowired
    private RpcServer server;
    @Autowired
    private RpcSerializer serializer;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private EnumDictResolver enumDictResolver;
    @Autowired
    private PropertyCaptionResolver propertyCaptionResolver;
    @Autowired
    private ModelValidationGenerator modelValidationGenerator;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (checkLan(request, response)) {
            return "/rpc/api";
        }
        return null;
    }

    private boolean checkLan(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (NetUtil.isLanIp(WebUtil.getRemoteAddrIp(request))) {
            return true;
        } else {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Non-LAN cann't access this page."); // 禁止非局域网访问
            return false;
        }
    }

    @RequestMapping("/modules")
    @ResponseBody
    public String modules(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (checkLan(request, response)) {
            Map<String, Object> beans = this.context.getBeansWithAnnotation(RpcController.class);
            Set<String> modules = new TreeSet<>();
            for (Entry<String, Object> entry : beans.entrySet()) {
                String beanId = entry.getKey();
                Object bean = entry.getValue();
                RpcControllerMeta controllerMeta = this.server.getMeta(beanId, bean);
                String module = controllerMeta.getModule();
                if (module != null) {
                    modules.add(module);
                }
            }
            return this.serializer.serialize(modules);
        }
        return null;
    }

    @RequestMapping("/beans")
    @ResponseBody
    // module参数中可能含有空格，不能作为路径参数
    public String beans(@RequestParam("module") String module, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (checkLan(request, response)) {
            Map<String, Object> beans = this.context.getBeansWithAnnotation(RpcController.class);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Entry<String, Object> entry : beans.entrySet()) {
                String beanId = entry.getKey();
                Object bean = entry.getValue();
                RpcControllerMeta controllerMeta = this.server.getMeta(beanId, bean);
                if (controllerMeta.getModule().equals(module)
                        && controllerMeta.getMethodNames().size() > 0) {
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("beanId", controllerMeta.getBeanId());
                    meta.put("caption", controllerMeta.getCaption());
                    meta.put("deprecated", controllerMeta.isDeprecated());
                    list.add(meta);
                }
            }
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                    String beanId1 = (String) map1.get("beanId");
                    String beanId2 = (String) map2.get("beanId");
                    return beanId1.compareTo(beanId2);
                }
            });
            return this.serializer.serialize(list);
        }
        return null;
    }

    @RequestMapping("/{beanId}/methods")
    @ResponseBody
    public String methods(@PathVariable("beanId") String beanId, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (checkLan(request, response)) {
            Object bean = SpringUtil.getBeanByName(this.context, beanId);
            if (bean != null) {
                RpcControllerMeta controllerMeta = this.server.getMeta(beanId, bean);
                Collection<RpcMethodMeta> methodMetas = controllerMeta.getMethodMetas();
                List<Map<String, Object>> list = new ArrayList<>();
                for (RpcMethodMeta methodMeta : methodMetas) {
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("name", methodMeta.getName());
                    meta.put("caption", methodMeta.getCaption());
                    meta.put("deprecated",
                            controllerMeta.isDeprecated() || methodMeta.isDeprecated());
                    meta.put("argCount", methodMeta.getArgMetas().size());
                    list.add(meta);
                }
                Collections.sort(list, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                        String name1 = (String) map1.get("name");
                        String name2 = (String) map2.get("name");
                        return name1.compareTo(name2);
                    }
                });
                return this.serializer.serialize(list);
            }
        }
        return null;
    }

    @RequestMapping("/{beanId}/{methodName}/{argCount}")
    @ResponseBody
    public String method(@PathVariable("beanId") String beanId,
            @PathVariable("methodName") String methodName, @PathVariable("argCount") int argCount,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (checkLan(request, response)) {
            Object bean = SpringUtil.getBeanByName(this.context, beanId);
            if (bean != null) {
                RpcControllerMeta controllerMeta = this.server.getMeta(beanId, bean);
                RpcMethodMeta methodMeta = controllerMeta.getMethodMeta(methodName, argCount);
                Map<String, Object> meta = new HashMap<>();
                meta.put("name", methodMeta.getName());
                meta.put("caption", methodMeta.getCaption());
                meta.put("deprecated", controllerMeta.isDeprecated() || methodMeta.isDeprecated());
                meta.put("argMetas", methodMeta.getArgMetas());
                meta.put("resultType", methodMeta.getResultType());
                meta.put("anonymous", methodMeta.isAnonymous());
                meta.put("lan", methodMeta.isLan());
                return this.serializer.serialize(meta);
            }
        }
        return null;
    }

    @RequestMapping("/{beanId}/{methodName}/{argCount}/arg/{argType}/properties") // 必须带后缀结尾，否则argType中包含.，将被识别为扩展名
    @ResponseBody
    public String argProperties(@PathVariable("beanId") String beanId,
            @PathVariable("methodName") String methodName, @PathVariable("argCount") int argCount,
            @PathVariable("argType") String argType, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (checkLan(request, response)) {
            RpcPort port = new RpcPort(beanId, methodName, argCount);
            Locale locale = SpringWebUtil.getLocale(request);
            Binate<Class<?>, List<RpcVariableMeta>> binate = argProperties(port, argType, locale);
            if (binate == null) {
                return "null";
            }
            Map<String, Object> result = new HashMap<>();
            result.put("caption", CaptionUtil.getCaption(binate.getLeft(), locale));
            result.put("properties", binate.getRight());
            return this.serializer.serialize(result);
        }
        return null;
    }

    @RpcMethod(result = @RpcResult(filter = {
            @RpcResultFilter(type = RpcVariableMeta.class, includes = { "caption", "typeName",
                    "validation", "items" }),
            @RpcResultFilter(type = EnumItem.class, includes = { "key", "caption" }) }))
    public Map<String, RpcVariableMeta> getArgPropertyMetas(String beanId, String methodName,
            int argCount, String argType) {
        RpcPort port = new RpcPort(beanId, methodName, argCount);
        Locale locale = SpringWebContext.getLocale();
        Map<String, RpcVariableMeta> map = new HashMap<>();
        Binate<Class<?>, List<RpcVariableMeta>> binate = argProperties(port, argType, locale);
        if (binate != null) {
            List<RpcVariableMeta> metas = binate.getRight();
            for (RpcVariableMeta meta : metas) {
                map.put(meta.getName(), meta);
            }
        }
        return map;
    }

    @RpcMethod(result = @RpcResult(filter = @RpcResultFilter(type = EnumItem.class, includes = {
            "key", "caption" })))
    public EnumType getArgEnumType(String beanId, String methodName, int argCount, int argIndex) {
        RpcPort port = new RpcPort(beanId, methodName, argCount);
        RpcVariableMeta argMeta = this.server.getArgMeta(port, argIndex);
        if (argMeta == null) {
            throw new IllegalArgumentException(
                    methodName + "(" + NoSuchRpcMethodException.getArgExpression(argCount)
                            + ") in RpcController(beanId='" + beanId
                            + "') don't contains arg(index=" + argIndex + ")");
        }
        RpcTypeMeta typeMeta = argMeta.getType();
        Class<?> clazz = typeMeta.getType();
        if (!clazz.isEnum()) {
            throw new IllegalArgumentException(
                    methodName + "(" + NoSuchRpcMethodException.getArgExpression(argCount)
                            + ") in RpcController(beanId='" + beanId + "') whose arg(index="
                            + argIndex + ") is not enum");
        }
        @SuppressWarnings("unchecked")
        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
        String subtype = this.server.getEnumSubType(port, enumClass);
        return getEnumType(enumClass, subtype);
    }

    @SuppressWarnings("unchecked")
    private Binate<Class<?>, List<RpcVariableMeta>> argProperties(RpcPort port, String argType,
            Locale locale) {
        RpcTypeMeta argTypeMeta = null;
        if (StringUtils.isNumeric(argType)) { // 参数类型为数字，则作为参数索引下标处理
            int argIndex = MathUtil.parseInt(argType);
            RpcVariableMeta argMeta = this.server.getArgMeta(port, argIndex);
            argTypeMeta = argMeta == null ? null : argMeta.getType();
        } else { // 参数的下级类型会直接指定参数类型，此时直接构建参数的类型元数据
            try {
                argTypeMeta = new RpcTypeMeta(this.context.getClassLoader().loadClass(argType));
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        if (argTypeMeta == null) {
            return null;
        }

        Class<?> clazz = argTypeMeta.getType();
        List<RpcVariableMeta> metas = null;
        if (clazz.isEnum()) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
            String subtype = this.server.getEnumSubType(port, enumClass);
            metas = getConstantMetas(enumClass, subtype, locale);
        } else if (ClassUtil.isComplex(clazz)) {
            Collection<PropertyMeta> propertyMetas = ClassUtil.findPropertyMetas(clazz, false, true,
                    true, argTypeMeta.getIncludes(), argTypeMeta.getExcludes());
            metas = getPropertyVariableMetas(port, clazz, propertyMetas, locale, true);
        }
        return new Binary<>(clazz, metas);
    }

    private List<RpcVariableMeta> getConstantMetas(Class<? extends Enum<?>> enumClass,
            String subtype, Locale locale) {
        EnumType enumType = this.enumDictResolver.getEnumType(enumClass.getName(), subtype, locale);
        if (enumType != null) {
            List<RpcVariableMeta> metas = new ArrayList<>();
            for (Enum<?> constant : enumClass.getEnumConstants()) {
                String name = constant.name();
                EnumItem item = enumType.getItem(name);
                if (item != null) {
                    RpcVariableMeta meta = new RpcVariableMeta(enumClass);
                    meta.setName(name);
                    meta.setCaption(item.getCaption());
                    metas.add(meta);
                }
            }
            return metas;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<RpcVariableMeta> getPropertyVariableMetas(RpcPort port, Class<?> clazz,
            Collection<PropertyMeta> propertyMetas, Locale locale, boolean validatable) {
        List<RpcVariableMeta> metas = new ArrayList<>();
        for (PropertyMeta propertyMeta : propertyMetas) {
            String propertyName = propertyMeta.getName();
            Class<?> propertyType = propertyMeta.getType();
            RpcVariableMeta meta = new RpcVariableMeta(propertyType);
            meta.setName(propertyName);
            meta.setCaption(
                    this.propertyCaptionResolver.resolveCaption(clazz, propertyName, locale));

            RpcTypeMeta typeMeta = meta.getType();
            if (Iterable.class.isAssignableFrom(propertyType)
                    || Map.class.isAssignableFrom(propertyType)) { // 处理元素类型
                for (Annotation annotation : propertyMeta.getAnnotations()) {
                    if (annotation instanceof ComponentType) {
                        ComponentType componentType = (ComponentType) annotation;
                        if (componentType.value() != Object.class) {
                            typeMeta.setComponentType(componentType.value());
                        }
                    }
                }
            } else if (propertyType.isEnum()) { // 处理枚举类型
                Class<? extends Enum<?>> propertyEnumClass = (Class<? extends Enum<?>>) propertyType;
                String subtype = this.server.getEnumSubType(port, propertyEnumClass);
                EnumType enumType = this.enumDictResolver.getEnumType(propertyEnumClass.getName(),
                        subtype, locale);
                if (enumType != null) {
                    meta.setItems(enumType.getItems());
                }
            }
            // 模型的属性生成校验规则
            if (validatable && Model.class.isAssignableFrom(clazz)) {
                Class<Model> modelClass = (Class<Model>) clazz;
                Map<String, Object> validation = this.modelValidationGenerator.generate(modelClass,
                        locale, propertyName);
                if (validation != null && !validation.isEmpty()) {
                    meta.setValidation(validation);
                }
            }
            metas.add(meta);

            propertyMeta.getAnnotations().forEach(annotation -> {
                if (annotation instanceof ValidationAlias) {
                    String alias = ((ValidationAlias) annotation).value();
                    if (StringUtils.isNotBlank(alias)) {
                        RpcVariableMeta aliasMeta = meta.clone();
                        aliasMeta.setName(alias);
                        metas.add(aliasMeta);
                    }
                }
            });
        }
        return metas;
    }

    @RequestMapping("/{beanId}/{methodName}/{argCount}/result/{className}/properties") // 必须带后缀结尾，否则className中包含.，将被识别为扩展名
    @ResponseBody
    public String resultProperties(@PathVariable("beanId") String beanId,
            @PathVariable("methodName") String methodName, @PathVariable("argCount") int argCount,
            @PathVariable("className") String className, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (checkLan(request, response)) {
            RpcPort port = new RpcPort(beanId, methodName, argCount);
            Locale locale = SpringWebUtil.getLocale(request);
            Binate<Class<?>, List<RpcVariableMeta>> binate = resultProperties(port, className,
                    locale);
            if (binate == null) {
                return "null";
            }
            Map<String, Object> result = new HashMap<>();
            result.put("caption", CaptionUtil.getCaption(binate.getLeft(), locale));
            result.put("properties", binate.getRight());
            return this.serializer.serialize(result);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Binate<Class<?>, List<RpcVariableMeta>> resultProperties(RpcPort port, String className,
            Locale locale) {
        Class<?> clazz;
        try {
            clazz = this.context.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }

        List<RpcVariableMeta> metas;
        if (clazz.isEnum()) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
            String subtype = this.server.getEnumSubType(port, enumClass);
            metas = getConstantMetas(enumClass, subtype, locale);
        } else {
            RpcResultFilter filter = this.server.getResultFilter(port, clazz);
            String[] includes = filter == null ? null : filter.includes();
            String[] excludues = filter == null ? null : filter.excludes();
            Collection<PropertyMeta> propertyMetas = ClassUtil.findPropertyMetas(clazz, true, false,
                    true, includes, excludues);
            metas = getPropertyVariableMetas(port, clazz, propertyMetas, locale, false);
        }
        return new Binary<>(clazz, metas);
    }

    @RequestMapping("/{beanId}/{methodName}/{argCount}/arg/{argType}/codes")
    @ResponseBody
    public String argCodes(@PathVariable("beanId") String beanId,
            @PathVariable("methodName") String methodName, @PathVariable("argCount") int argCount,
            @PathVariable("argType") String argType, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (checkLan(request, response)) {
            RpcPort port = new RpcPort(beanId, methodName, argCount);
            Locale locale = SpringWebUtil.getLocale(request);
            Binate<Class<?>, List<RpcVariableMeta>> binate = argProperties(port, argType, locale);
            if (binate == null) {
                return "null";
            }
            // TODO 待代码生成器
        }
        return null;
    }

    @RequestMapping("/{beanId}/{methodName}/{argCount}/result/{className}/codes")
    @ResponseBody
    public String resultCodes(@PathVariable("beanId") String beanId,
            @PathVariable("methodName") String methodName, @PathVariable("argCount") int argCount,
            @PathVariable("className") String className, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (checkLan(request, response)) {
            RpcPort port = new RpcPort(beanId, methodName, argCount);
            Locale locale = SpringWebUtil.getLocale(request);
            Binate<Class<?>, List<RpcVariableMeta>> binate = resultProperties(port, className,
                    locale);
            if (binate == null) {
                return "null";
            }
            // TODO 待代码生成器
        }
        return null;
    }
}
