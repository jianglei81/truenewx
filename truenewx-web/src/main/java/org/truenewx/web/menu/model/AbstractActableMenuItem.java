package org.truenewx.web.menu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 抽象的动作型菜单项
 *
 * @author jianglei
 * @since JDK 1.8
 */
abstract class AbstractActableMenuItem extends MenuItem {

    private static final long serialVersionUID = -8418975142325105071L;

    /**
     * 地域说明集合（菜单显示）
     */
    private Map<Locale, String> captions = new HashMap<>();
    /**
     * 图标
     */
    private String icon;

    /**
     * 子项集合
     */
    private List<MenuItem> subs = new ArrayList<>();

    private MenuItemAction action;

    public AbstractActableMenuItem(String type, String caption, String icon,
            MenuItemAction action) {
        super(type);
        this.captions.put(Locale.getDefault(), caption);
        this.icon = icon;
        this.action = action;
    }

    /**
     * @return 地域说明集合
     */
    public Map<Locale, String> getCaptions() {
        return this.captions;
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
