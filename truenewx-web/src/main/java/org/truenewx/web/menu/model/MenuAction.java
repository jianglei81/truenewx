package org.truenewx.web.menu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.truenewx.web.http.HttpLink;

/**
 * 菜单动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class MenuAction implements Serializable {

    private static final long serialVersionUID = -3489116426777799955L;

    /**
     * 权限名称
     */
    private String auth;
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
     * @param auth
     *            权限名称
     * @param caption
     *            菜单说明
     */
    public MenuAction(final String auth, final String caption) {
        this.auth = auth;
        if (caption != null) {
            this.captions.put(Locale.getDefault(), caption);
        }
    }

    /**
     * @return 权限名称
     */
    public String getAuth() {
        return this.auth;
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

    public String getAuth(final String href, final HttpMethod method) {
        for (final HttpLink link : this.links) {
            if (link.isMatched(href, method)) {
                return this.auth;
            }
        }
        return null;
    }

    public abstract String getAuth(String beanId, String methodName, Integer argCount);

}
