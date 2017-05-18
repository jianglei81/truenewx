package org.truenewx.web.security.login;

/**
 * 登出信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface LogoutInfo {

    /**
     *
     * @return 是否登出时调用HttpSession.invalidate()方法
     */
    boolean isInvalidatingSession();

    /**
     *
     * @return 需要在登出之后移除的cookie名称集合
     */
    Iterable<String> getCookieNames();

}
