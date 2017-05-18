package org.truenewx.web.security.authority;

import java.util.HashSet;
import java.util.Set;

/**
 * 默认的授权信息实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DefaultAuthorizationInfo implements AuthorizationInfo {

    private Set<String> roles = new HashSet<>();

    private Set<String> permissions = new HashSet<>();

    private boolean caching = true;

    public DefaultAuthorizationInfo(final boolean caching) {
        this.caching = caching;
    }

    @Override
    public Iterable<String> getRoles() {
        return this.roles;
    }

    @Override
    public Iterable<String> getPermissions() {
        return this.permissions;
    }

    @Override
    public boolean isCaching() {
        return this.caching;
    }

    public void addRole(final String role) {
        this.roles.add(role);
    }

    public void addPermission(final String permission) {
        this.permissions.add(permission);
    }

}
