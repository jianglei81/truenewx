package org.truenewx.web.menu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.truenewx.web.http.HttpLink;
import org.truenewx.web.rpc.RpcPort;
import org.truenewx.web.security.authority.Authority;

/**
 * 抽象的菜单项
 *
 * @author jianglei
 * @since JDK 1.8
 */
abstract class AbstractMenuItem {

    /**
     * 可见环境集合
     */
    private Set<String> profiles = new HashSet<>();
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
     * 链接集合
     */
    private List<HttpLink> links = new ArrayList<>();
    /**
     * RPC端口集合
     */
    private List<RpcPort> rpcs = new ArrayList<>();
    /**
     * 图标
     */
    private String icon;
    /**
     * 子项集合
     */
    private List<MenuItem> subs = new ArrayList<>();
    /**
     * 是否隐藏
     */
    private boolean hidden;

    public AbstractMenuItem(final Authority authority, final String caption, final HttpLink link,
            final String icon, final boolean hidden) {
        this.authority = authority;
        this.captions.put(Locale.getDefault(), caption);
        this.links.add(link);
        this.icon = icon;
        this.hidden = hidden;
    }

    /**
     *
     * @return 可见环境集合
     */
    public Set<String> getProfiles() {
        return this.profiles;
    }

    /**
     *
     * @return 所需授权
     */
    public Authority getAuthority() {
        return this.authority;
    }

    /**
     * @return 地域说明集合
     */
    public Map<Locale, String> getCaptions() {
        return this.captions;
    }

    /**
     * @return 权限类型
     */
    public Map<String, Object> getOptions() {
        return this.options;
    }

    /**
     * 连接集合
     */
    public List<HttpLink> getLinks() {
        return this.links;
    }

    /**
     * RPC端口集合
     */
    public List<RpcPort> getRpcs() {
        return this.rpcs;
    }

    /**
     * 图标
     */
    public String getIcon() {
        return this.icon;
    }

    /**
     * @return 子项集合
     */
    public List<MenuItem> getSubs() {
        return this.subs;
    }

    /**
     *
     * @return 是否隐藏
     */
    public boolean isHidden() {
        return this.hidden;
    }

}
