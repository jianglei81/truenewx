package org.truenewx.web.menu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.truenewx.core.spring.core.env.functor.FuncProfile;
import org.truenewx.web.http.HttpLink;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;

/**
 * 菜单动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class MenuAction implements Serializable {

    private static final long serialVersionUID = -3489116426777799955L;

    /**
     * 所需授权
     */
    private Authority authority;
    /**
     * 地域说明集合（菜单显示）
     */
    private Map<Locale, String> captions = new HashMap<>();
    /**
     * 配置类型
     */
    private Map<String, Object> options = new HashMap<>();
    /**
     * 连接集合
     */
    private List<HttpLink> links = new ArrayList<>();
    /**
     * 可见环境集合
     */
    private Set<String> profiles = new LinkedHashSet<>();

    /**
     *
     * @param authority
     *            所需授权
     * @param caption
     *            菜单说明
     */
    public MenuAction(final Authority authority, final String caption) {
        this.authority = authority;
        if (caption != null) {
            this.captions.put(Locale.getDefault(), caption);
        }
    }

    /**
     *
     * @return 所需授权
     */
    public Authority getAuthority() {
        return this.authority;
    }

    /**
     * 相当于获取 auth.role
     *
     * @return 所需角色
     */
    public String getRole() {
        return this.authority.getRole();
    }

    /**
     * 相当于获取 auth.permission
     * 
     * @return 所需权限
     */
    public String getPermission() {
        return this.authority.getPermission();
    }

    /**
     * @return 权限类型
     */
    public Map<String, Object> getOptions() {
        return this.options;
    }

    /**
     * @return 地域说明集合
     */
    public Map<Locale, String> getCaptions() {
        return this.captions;
    }

    public String getCaption() {
        return this.captions.get(Locale.getDefault());
    }

    /**
     * 连接集合
     */
    public List<HttpLink> getLinks() {
        return this.links;
    }

    /**
     *
     * @return 可见环境集合
     */
    public Set<String> getProfiles() {
        return this.profiles;
    }

    /**
     * 判断当前菜单动作在指定环境中是否可见
     *
     * @param profile
     *            环境
     * @return 当前菜单动作在指定环境中是否可见
     */
    public boolean isVisible(final String profile) {
        return this.profiles.isEmpty() || this.profiles.contains(profile);
    }

    public boolean contains(final String href, final HttpMethod method) {
        for (final HttpLink link : getLinks()) {
            if (link.isMatched(href, method)) {
                return true;
            }
        }
        return false;
    }

    public Authority getAuthority(final String href, final HttpMethod method) {
        for (final HttpLink link : this.links) {
            if (link.isMatched(href, method)) {
                return this.authority;
            }
        }
        return null;
    }

    public abstract Authority getAuthority(String beanId, String methodName, Integer argCount);

    public boolean matchesProfile() {
        final String profile = FuncProfile.INSTANCE.apply();
        return StringUtils.isBlank(profile) || this.profiles.isEmpty()
                || this.profiles.contains(profile);
    }

    public boolean matchesAuth(Authorization authorization) {
        // 如果当前动作未指定授权，表示没有授权限制，视为匹配
        return this.authority == null || this.authority.isContained(authorization);
    }

}
