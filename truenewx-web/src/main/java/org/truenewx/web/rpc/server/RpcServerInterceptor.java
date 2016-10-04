package org.truenewx.web.rpc.server;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RPC服务端拦截器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface RpcServerInterceptor {
    /**
     * 获取指定bean的RPC方法名清单前调用
     *
     * @param bean
     *            提供RPC方法的bean
     * @throws Exception
     *             如果调用过程中出现错误
     */
    void beforeMethods(Object bean) throws Exception;

    /**
     * 在执行指定RPC方法前调用
     *
     * @param beanId
     *            Spring中的bean id
     * @param method
     *            被调用的RPC方法
     * @param request
     *            HTTP请求
     * @param response
     *            HTTP响应
     * @throws Exception
     *             如果调用过程中出现错误
     */
    void beforeInvoke(String beanId, Method method, HttpServletRequest request,
            HttpServletResponse response) throws Exception;
}
