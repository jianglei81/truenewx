package org.truenewx.web.rpc.server;

import java.lang.reflect.Method;

/**
 * RPC服务端拦截器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface RpcInvokeInterceptor {
    /**
     * 在执行指定RPC方法前调用
     *
     * @param beanId
     *            Spring中的bean id
     * @param method
     *            被调用的RPC方法
     * @param args
     *            调用参数集
     * @throws Exception
     *             如果调用过程中出现错误
     */
    void beforeInvoke(String beanId, Method method, Object[] args) throws Exception;

    /**
     * 在执行指定RPC方法后调用
     *
     * @param beanId
     *            Spring中的bean id
     * @param method
     *            被调用的RPC方法
     * @param args
     *            调用参数集
     * @param result
     *            调用结果
     * @throws Exception
     *             如果调用过程中出现错误
     */
    void afterInvoke(String beanId, Method method, Object[] args, Object result) throws Exception;
}
