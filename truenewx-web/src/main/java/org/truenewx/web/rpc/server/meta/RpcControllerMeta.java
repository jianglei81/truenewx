package org.truenewx.web.rpc.server.meta;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;

/**
 * RPC控制器元数据
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcControllerMeta implements Comparable<RpcControllerMeta> {
    /**
     * RPC控制器所属根包的后缀
     */
    private static final String ROOT_PACKAGE_SUFFIX = ".controller";

    private String beanId;
    private Object controller;
    private List<RpcMethodMeta> methodMetas;

    public RpcControllerMeta(String beanId, Object controller) {
        checkNotNull(beanId);
        checkNotNull(controller);
        this.beanId = beanId;
        this.controller = controller;
    }

    public String getBeanId() {
        return this.beanId;
    }

    public Object getController() {
        return this.controller;
    }

    public String getCaption() {
        RpcController rpcController = this.controller.getClass().getAnnotation(RpcController.class);
        return rpcController == null ? null : rpcController.caption();
    }

    public String getModule() {
        Class<?> beanClass = this.controller.getClass();
        RpcController rpcController = beanClass.getAnnotation(RpcController.class);
        if (rpcController != null) {
            String module = rpcController.module();
            if (StringUtils.isBlank(module)) {
                String packageName = beanClass.getPackage().getName();
                int index = packageName.indexOf(ROOT_PACKAGE_SUFFIX);
                if (index > 0) {
                    module = packageName.substring(index + ROOT_PACKAGE_SUFFIX.length());
                    if (StringUtils.isBlank(module)) { // 正好位于根包下，则视为默认模块
                        module = " default";
                    } else { // 去掉开头的句点
                        module = module.substring(1);
                    }
                } else { // 不属于默认根包下，则视为属于框架模块，确保其位于默认模块之前
                    module = "  framework";
                }
            }
            return module;
        }
        return null;
    }

    @Override
    public int compareTo(RpcControllerMeta other) {
        return this.beanId.compareTo(other.beanId);
    }

    /**
     * 获取所有RPC方法
     *
     * @return 所有RPC方法
     */
    public Collection<RpcMethodMeta> getMethodMetas() {
        if (this.methodMetas == null) {
            synchronized (this.beanId) {
                if (this.methodMetas == null) {
                    this.methodMetas = new ArrayList<>();
                    Method[] methods;
                    try {
                        methods = this.controller.getClass().getMethods();
                    } catch (SecurityException e) {
                        methods = new Method[0];
                    }
                    // 从所有方法中查找
                    for (Method method : methods) {
                        if (isRpcMethod(method)) {
                            this.methodMetas.add(new RpcMethodMeta(method));
                        }
                    }
                    Collections.sort(this.methodMetas);
                }
            }
        }
        return this.methodMetas;
    }

    /**
     * 判断指定方法是否有效的RPC方法
     *
     * @param method
     *            方法
     * @return 是否有效的RPC方法
     */
    private boolean isRpcMethod(Method method) {
        return method.getAnnotation(RpcMethod.class) != null && !method.isVarArgs()
                && !method.isBridge() && Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers());
    }

    public Set<String> getMethodNames() {
        Set<String> methodNames = new HashSet<>();
        Collection<RpcMethodMeta> methods = getMethodMetas();
        for (RpcMethodMeta method : methods) {
            methodNames.add(method.getName());
        }
        return methodNames;
    }

    /**
     * 获取具有指定名称和参数个数的方法
     *
     * @param methodName
     *            方法名称
     * @param argCount
     *            参数个数，为null时不限参数个数
     * @param version
     *            版本号
     * @return 方法
     * @throws DuplicatedRpcMethodException
     *             如果存在多个这种方法
     * @throws NoSuchRpcMethodException
     *             如果不存在这种方法
     */
    public Method getMethod(String methodName, Integer argCount, String version)
            throws DuplicatedRpcMethodException, NoSuchRpcMethodException {
        Method result = null;
        for (RpcMethodMeta methodMeta : getMethodMetas()) {
            Method method = methodMeta.getMethod();
            String methodVersion = methodMeta.getVersion();
            if (method.getName().equals(methodName)
                    && (argCount == null || method.getParameterTypes().length == argCount)
                    && (StringUtils.isBlank(version) || StringUtils.isBlank(methodVersion)
                            || version.equals(methodVersion))) {
                if (result != null) {
                    throw new DuplicatedRpcMethodException(this.controller.getClass(), methodName,
                            argCount);
                }
                result = method;
            }
        }
        if (result == null) {
            throw new NoSuchRpcMethodException(this.controller.getClass(), methodName, argCount);
        }
        return result;
    }

    /**
     * 获取当前控制器中指定名称和参数个数的方法的元数据
     *
     * @param methodName
     *            方法名
     * @param argCount
     *            参数个数
     * @return 匹配的方法元数据
     */
    public RpcMethodMeta getMethodMeta(String methodName, int argCount) {
        for (RpcMethodMeta methodMeta : getMethodMetas()) {
            if (methodMeta.getName().equals(methodName)
                    && methodMeta.getArgMetas().size() == argCount) {
                return methodMeta;
            }
        }
        return null;
    }

    /**
     *
     * @return 是否已不推荐使用
     */
    public boolean isDeprecated() {
        return this.controller.getClass().getAnnotation(Deprecated.class) != null;
    }

}
