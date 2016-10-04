package org.truenewx.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.truenewx.core.exception.HandleableException;

/**
 * 自动登录器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AutoLoginer extends LoginPredicate {

    /**
     *
     * @return 保存登录名的Cookie名称
     */
    String getLoginNameCookieName();

    /**
     *
     * @return 保存密码的Cookie名称
     */
    String getPasswordCookieName();

    /**
     * 执行自动登录
     *
     * @param loginName
     *            登录名
     * @param password
     *            密码
     * @param request
     *            请求
     * @param response
     *            响应
     * @throws HandleableException
     *             如果登录失败
     */
    void login(String loginName, String password, HttpServletRequest request,
                    HttpServletResponse response) throws HandleableException;

}
