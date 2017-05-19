package org.truenewx.web.security.authority;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.util.CollectionUtil;

/**
 * 授权=角色+权限
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class Authority {

    private String role;
    private String permission;

    public Authority(final String role, final String permission) {
        this.role = role;
        this.permission = permission;
    }

    public String getRole() {
        return this.role;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean isNotEmpty() {
        return StringUtils.isNotEmpty(this.role) || StringUtils.isNotEmpty(this.permission);
    }

    /**
     * 判断当前授权是否被包含在指定的授权集中
     *
     * @param authorization
     *            授权集
     *
     * @return 当前授权是否被包含在指定的授权集中
     */
    public boolean isContained(final Authorization authorization) {
        if (authorization == null) {
            return false;
        }
        Iterable<String> roles = authorization.getRoles();
        if (roles == null) {
            roles = Collections.emptyList();
        }
        Iterable<String> permissions = authorization.getPermissions();
        if (permissions == null) {
            permissions = Collections.emptyList();
        }
        // 当前角色/权限为空则不校验视为包含，指定角色/权限集合中包含*则视为包含所有角色/权限
        return (StringUtils.isEmpty(this.role) || CollectionUtil.contains(roles, Strings.ASTERISK)
                || CollectionUtil.contains(roles, this.role))
                && (StringUtils.isEmpty(this.permission)
                        || CollectionUtil.contains(permissions, Strings.ASTERISK)
                        || CollectionUtil.contains(permissions, this.permission));
    }

}
