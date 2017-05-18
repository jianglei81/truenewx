package org.truenewx.web.security.login;

import javax.servlet.http.Cookie;

/**
 * 登录信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface LoginInfo {

    Object getUser();

    Iterable<Cookie> getCookies();

}
