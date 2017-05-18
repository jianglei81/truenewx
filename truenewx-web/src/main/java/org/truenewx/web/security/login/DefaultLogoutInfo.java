package org.truenewx.web.security.login;

import java.util.HashSet;
import java.util.Set;

/**
 * 默认的登出信息实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DefaultLogoutInfo implements LogoutInfo {

    private boolean invalidatingSession;
    private Set<String> cookieNames = new HashSet<>();

    public DefaultLogoutInfo(final boolean invalidatingSession) {
        this.invalidatingSession = invalidatingSession;
    }

    @Override
    public boolean isInvalidatingSession() {
        return this.invalidatingSession;
    }

    @Override
    public Iterable<String> getCookieNames() {
        return this.cookieNames;
    }

    public void addCookieName(final String name) {
        this.cookieNames.add(name);
    }

}
