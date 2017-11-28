package org.truenewx.web.menu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
     * 地域说明集合（菜单显示）
     */
    private Map<Locale, String> captions = new HashMap<>();
    /**
     * 配置类型
     */
    private Map<String, Object> options = new HashMap<>();
    /**
     * 图标
     */
    private String icon;

    /**
     * 子项集合
     */
    private List<MenuItem> subs = new ArrayList<>();

    private MenuItemAction action;

    public AbstractMenuItem(final String caption, final String icon, final MenuItemAction action) {
        this.captions.put(Locale.getDefault(), caption);
        this.icon = icon;
        this.action = action;
    }

    /**
     *
     * @return 可见环境集合
     */
    public Set<String> getProfiles() {
        return this.profiles;
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
     * @return 菜单项动作
     */
    public MenuItemAction getAction() {
        return this.action;
    }

}
