package org.truenewx.web.security.login;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

/**
 * 默认的登录信息实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DefaultLoginInfo implements LoginInfo {

    private Object user;

    private List<Cookie> cookies = new ArrayList<>();

    public DefaultLoginInfo(final Object user) {
        this.user = user;
    }

    @Override
    public Object getUser() {
        return this.user;
    }

    @Override
    public Iterable<Cookie> getCookies() {
        return this.cookies;
    }

    public void addCookie(final Cookie cookie) {
        this.cookies.add(cookie);
    }

}
