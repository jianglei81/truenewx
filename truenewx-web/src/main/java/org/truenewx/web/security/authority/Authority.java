package org.truenewx.web.security.authority;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    private Map<Locale, String> captions = new HashMap<>();
    /**
     * 登录即可访问的权限
     */
    public static final Authority LOGINED = new Authority(":logined", null);

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

    public Map<Locale, String> getCaptions() {
        return this.captions;
    }

    public void setCaption(String caption) {
        this.captions.put(Locale.getDefault(), caption);
    }

    public String getCaption() {
        return this.captions.get(Locale.getDefault());
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
        if (this == LOGINED) { // 当前授权如果为登录即可访问，authorization不为null，则为已登录，此时返回true
            return true;
        }
        Iterable<String> roles = authorization.getRoles();
        if (roles == null) {
            roles = Collections.emptyList();
        }
        Iterable<String> permissions = authorization.getPermissions();
        if (permissions == null) {
            permissions = Collections.emptyList();
        }
        boolean roleContained = true;
        if (StringUtils.isNotEmpty(this.role)) { // 当前授权包含角色限定才校验角色
            // 被校验角色中包含*通配符角色，或者包含当前授权的角色，则视为角色匹配
            roleContained = CollectionUtil.contains(roles, Strings.ASTERISK)
                    || CollectionUtil.contains(roles, this.role);
        }
        boolean permissionContained = true;
        if (StringUtils.isNotEmpty(this.permission)) { // 当前授权包含权限限定才校验权限
            // 被校验权限中包含*通配符权限，或者包含当前授权的权限，则视为权限匹配
            permissionContained = CollectionUtil.contains(permissions, Strings.ASTERISK)
                    || CollectionUtil.contains(permissions, this.permission);
        }
        // 角色和权限均需匹配
        return roleContained && permissionContained;
    }

    @Override
    public String toString() {
        return "role=" + this.role + ", permission=" + this.permission;
    }

}
