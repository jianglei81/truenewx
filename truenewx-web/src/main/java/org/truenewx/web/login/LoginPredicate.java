package org.truenewx.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录判定
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface LoginPredicate {
    /**
     * 判定是否已经登录
     *
     * @param request
     *            HTTP请求
     * @param response
     *            HTTP响应，为null时不能进行诸如Cookie等基于响应的操作
     * @return 是否已经登录
     */
    boolean isLogined(HttpServletRequest request, HttpServletResponse response);
}
