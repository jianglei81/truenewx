package org.truenewx.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface LoginValidator {

    /**
     * 校验登录，返回用户未登录时应该要跳转到的登录页面URL，返回null表示校验通过
     *
     * @param request
     *            请求
     * @param response
     *            响应
     * @return 登录页面URL
     */
    String validateLogin(HttpServletRequest request, HttpServletResponse response);
}
