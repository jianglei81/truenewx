package org.truenewx.web.rpc.server;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.truenewx.web.rpc.RpcPort;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;
import org.truenewx.web.rpc.server.meta.RpcControllerMeta;
import org.truenewx.web.rpc.server.meta.RpcVariableMeta;

/**
 * PRC服务端
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface RpcServer {

    /**
     * 获取指定bean的所有RPC方法名清单
     *
     * @param beanId
     *            bean id
     * @return 指定bean的所有RPC方法名清单
     * @throws Exception
     *             如果获取过程中出现错误
     */
    Collection<String> methods(String beanId) throws Exception;

    /**
     * 执行指定RPC bean的指定方法
     *
     * @param beanId
     *            bean id
     * @param methodName
     *            方法名
     * @param argString
     *            参数序列化字符串
     * @param request
     *            HTTP请求
     * @param response
     *            HTTP响应
     * @return 执行结果
     * @throws Exception
     *             如果执行过程中出现错误
     */
    RpcInvokeResult invoke(String beanId, String methodName, String argString,
                    HttpServletRequest request, HttpServletResponse response) throws Throwable;

    /**
     * 获取指定bean的RPC元数据
     *
     * @param beanId
     *            bean id
     * @param bean
     *            bean
     * @return 指定bean的RPC元数据
     */
    RpcControllerMeta getMeta(String beanId, Object bean);

    /**
     * 获取指定枚举类型在指定RPC方法中的子类型名
     * @param port TODO
     * @param enumClass
     *            枚举类型
     *
     * @return 子类型名
     */
    String getEnumSubType(RpcPort port, Class<? extends Enum<?>> enumClass);

    /**
     * 获取指定bean中的指定方法的指定参数的元数据
     * @param port TODO
     * @param argIndex
     *            方法参数索引下标
     *
     * @return 参数的元数据
     */
    RpcVariableMeta getArgMeta(RpcPort port, int argIndex);

    /**
     * 获取指定RPC方法的结果过滤
     * @param port TODO
     * @param resultType
     *            结果类型中的过滤类型
     *
     * @return 结果过滤
     */
    RpcResultFilter getResultFilter(RpcPort port, Class<?> resultType);

}
