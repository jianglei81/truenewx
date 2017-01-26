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
import org.truenewx.core.util.ArrayUtil;
import org.truenewx.core.util.BeanUtil;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.CollectionUtil;
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
import org.truenewx.web.spring.context.SpringWebContext;
import org.truenewx.web.spring.util.SpringWebUtil;

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
    private RpcServerInterceptor interceptor;
    private StringSerializer serializer;
    private Map<String, RpcControllerMeta> metaMap = new HashMap<>();

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }

    private ApplicationContext getContext() {
        if (this.context.getParent() == null) { // 若为根上下文，尝试从WEB容器中获取下级上下文
            final HttpServletRequest request = SpringWebContext.getRequest();
            if (request != null) {
                final ApplicationContext context = SpringWebUtil.getApplicationContext(request);
                if (context != null) {
                    this.context = context;
                }
            }
        }
        return this.context;
    }

    public void setInterceptor(final RpcServerInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Autowired
    public void setSerializer(final StringSerializer serializer) {
        this.serializer = serializer;
    }

    private RpcControllerMeta getMeta(final String beanId) {
        RpcControllerMeta meta = this.metaMap.get(beanId);
        if (meta == null) {
            final Object bean = getContext().getBean(beanId);
            meta = buildMeta(beanId, bean);
        }
        return meta;
    }

    private RpcControllerMeta buildMeta(final String beanId, final Object bean) {
        if (bean.getClass().getAnnotation(RpcController.class) == null) {
            throw new IllegalArgumentException(
                    "The '" + beanId + "' is not an " + RpcController.class.getSimpleName());
        }
        final RpcControllerMeta meta = new RpcControllerMeta(beanId, bean);
        this.metaMap.put(beanId, meta);
        return meta;
    }

    @Override
    public Collection<String> methods(final String beanId) throws Exception {
        final RpcControllerMeta meta = getMeta(beanId);
        if (this.interceptor != null) {
            this.interceptor.beforeMethods(meta.getController());
        }
        return meta.getMethodNames();
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
    public RpcInvokeResult invoke(final String beanId, final String methodName,
            final String argString, final HttpServletRequest request,
            final HttpServletResponse response) throws Throwable {
        final RpcControllerMeta meta = getMeta(beanId);
        Method method;
        Object[] args;
        if (argString == null) { // Map形式的参数集
            method = meta.getMethod(methodName, null);
            final Class<?>[] declaredArgTypes = method.getParameterTypes();
            final RpcArg[] rpcArgs = method.getAnnotation(RpcMethod.class).args();
            args = new Object[declaredArgTypes.length];
            for (int i = 0; i < declaredArgTypes.length; i++) {
                final Class<?> argType = declaredArgTypes[i];
                final RpcArg rpcArg = ArrayUtil.get(rpcArgs, i);
                String argValueString = null;
                if (rpcArg != null) {
                    final String parameterName = rpcArg.name();
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
            final Class<?>[] declaredArgTypes = method.getParameterTypes(); // 声明参数类型集
            final RpcArg[] rpcArgs = method.getAnnotation(RpcMethod.class).args();
            for (int i = 0; i < declaredArgTypes.length; i++) {
                final Class<?> argType = declaredArgTypes[i];
                // 集合或数组类型参数需重新按照元素类型反序列化
                final RpcArg rpcArg = ArrayUtil.get(rpcArgs, i);
                if ((Collection.class.isAssignableFrom(argType) && rpcArg != null
                        && rpcArg.componentType() != Object.class)
                        || (argType.isArray() && ClassUtil.isComplex(argType.getComponentType()))) {
                    final String argValueString = this.serializer.serializeBean(args[i]);
                    args[i] = deserializeArgValue(argValueString, argType, rpcArg);
                }
            }
            final Class<?>[] argTypes = getArgTypes(args); // 实际参数类型集
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
            // 调用拦截器
            if (this.interceptor != null) {
                this.interceptor.beforeInvoke(beanId, method, request, response);
                if (response.getStatus() != HttpStatus.OK.value()) {
                    return new RpcInvokeResult(Strings.EMPTY);
                }
            }
            final Object result = method.invoke(meta.getController(), args);
            final RpcResult rpcResult = method.getAnnotation(RpcMethod.class).result();
            final RpcResultFilter[] resultFilters = rpcResult.filter();
            return new RpcInvokeResult(result, resultFilters);
        } catch (final HandleableException e) {
            throw e;
        } catch (final Exception e) {
            e.printStackTrace();
            throw e.getCause();
        }
    }

    @SuppressWarnings("unchecked")
    private void transferArgValue(final Method method, final Class<?>[] declaredArgTypes,
            final Object[] args, final int i)
            throws InstantiationException, IllegalAccessException {
        final Class<?> declaredArgType = declaredArgTypes[i];
        final Object arg = args[i];
        final Class<?> actualArgType = arg.getClass();
        if (Collection.class.isAssignableFrom(declaredArgType)) { // 声明为集合
            if (actualArgType.isArray()) { // 实际为数组
                final Collection<?> newCollection = (Collection<?>) declaredArgType.newInstance();
                final Object array = arg;
                CollectionUtils.mergeArrayIntoCollection(array, newCollection);
                args[i] = newCollection;
            } else if (Collection.class.isAssignableFrom(actualArgType)) { // 实际也为集合
                final RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
                final RpcArg rpcArg = ArrayUtil.get(rpcMethod.args(), i);
                if (rpcArg != null) {
                    final Class<?> componentType = rpcArg.componentType();
                    if (componentType != Object.class) {
                        @SuppressWarnings("rawtypes")
                        final Collection newCollection = (Collection) actualArgType.newInstance();
                        for (final Object obj : (Collection<?>) arg) {
                            if (obj instanceof Map) { // 元素为Map才可转换
                                final Map<String, Object> map = (Map<String, Object>) obj;
                                final Object bean = BeanUtil.toBean(map, componentType);
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
            final Collection<?> collection = (Collection<?>) arg;
            final Class<?> componentType = declaredArgType.getComponentType();
            args[i] = Array.newInstance(componentType, collection.size());
            int j = 0;
            for (Object value : collection) {
                if (value instanceof Map && !Map.class.isAssignableFrom(componentType)
                        && !componentType.isPrimitive()) { // 实际值为Map，期望值为复合对象
                    value = BeanUtil.toBean((Map<String, Object>) value, componentType);
                }
                Array.set(arg, j++, value);
            }
        } else if (Map.class.isAssignableFrom(declaredArgType)
                && Map.class.isAssignableFrom(actualArgType)) { // 声明和实际均为Map
            final RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            final RpcArg rpcArg = ArrayUtil.get(rpcMethod.args(), i);
            if (rpcArg != null) {
                final Class<?> componentType = rpcArg.componentType();
                if (componentType != Object.class) {
                    final Map<String, Object> map = (Map<String, Object>) arg;
                    for (final Entry<String, Object> entry : map.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof Map) { // Map的值为Map才可以转换
                            value = BeanUtil.toBean((Map<String, Object>) value, componentType);
                            entry.setValue(value);
                        }
                    }
                }
            }
        } else if (Number.class.isAssignableFrom(declaredArgType)
                && Number.class.isAssignableFrom(actualArgType)) { // 声明和实际均为数字
            args[i] = this.serializer.deserializeBean(this.serializer.serializeBean(arg),
                    declaredArgType);
        }
    }

    private Object deserializeArgValue(final String argValueString, final Class<?> argType,
            final RpcArg rpcArg) {
        if (Collection.class.isAssignableFrom(argType)) { // 声明参数类型为集合，则按指定元素类型反序列化
            final Class<?> elementType = rpcArg != null ? rpcArg.componentType() : Object.class;
            return this.serializer.deserializeList(argValueString, elementType);
        } else if (argType.isArray()) { // 声明参数类型为数组，则按数组元素类型反序列化
            Object[] array = this.serializer.deserializeArray(argValueString);
            final Class<?> elementType = argType.getComponentType();
            if (elementType != Object.class && ClassUtil.isComplex(elementType)) { // 数组元素为复合类型，则需要转换为复合类型
                final Class<?>[] elementTypes = new Class<?>[array.length];
                for (int j = 0; j < array.length; j++) {
                    elementTypes[j] = elementType;
                }
                array = this.serializer.deserializeArray(argValueString, elementTypes);
            }
            return array;
        } else { // 默认按声明参数类型反序列化
            return this.serializer.deserializeBean(argValueString, argType);
        }
    }

    private Class<?>[] getArgTypes(final Object[] args) {
        final Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            final Object arg = args[i];
            types[i] = arg == null ? null : arg.getClass();
        }
        return types;
    }

    @Override
    public RpcControllerMeta getMeta(final String beanId, final Object bean) {
        RpcControllerMeta meta = this.metaMap.get(beanId);
        if (meta == null) {
            meta = buildMeta(beanId, bean);
        }
        return meta;
    }

    @Override
    public RpcVariableMeta getArgMeta(final String beanId, final String methodName,
            final int argCount, final int argIndex) {
        final RpcControllerMeta meta = getMeta(beanId);
        try {
            final RpcMethodMeta methodMeta = meta.getMethodMeta(methodName, argCount);
            if (methodMeta != null) {
                return CollectionUtil.get(methodMeta.getArgMetas(), argIndex);
            }
        } catch (final Exception e) {
        }
        return null;
    }

    @Override
    public String getEnumSubType(final String beanId, final String methodName, final int argCount,
            final Class<? extends Enum<?>> enumClass) {
        final RpcControllerMeta meta = getMeta(beanId);
        try {
            final Method method = meta.getMethod(methodName, argCount);
            final RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            if (rpcMethod != null) {
                for (final RpcEnum rpcEnum : rpcMethod.enums()) {
                    if (rpcEnum.type().equals(enumClass)) {
                        return rpcEnum.sub();
                    }
                }
            }
        } catch (final Exception e) {
        }
        return null;
    }

    @Override
    public RpcResultFilter getResultFilter(final String beanId, final String methodName,
            final int argCount, final Class<?> resultType) {
        final RpcControllerMeta meta = getMeta(beanId);
        try {
            final Method method = meta.getMethod(methodName, argCount);
            final RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            if (rpcMethod != null) {
                final RpcResultFilter[] filters = rpcMethod.result().filter();
                for (final RpcResultFilter filter : filters) {
                    if (filter.type() == resultType) {
                        return filter;
                    }
                }
            }
        } catch (final Exception e) {
        }
        return null;
    }
}
