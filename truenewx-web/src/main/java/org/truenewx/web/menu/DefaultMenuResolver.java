package org.truenewx.web.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.menu.model.MenuItem;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;

/**
 * 默认菜单解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DefaultMenuResolver implements MenuResolver, InitializingBean {

    private Menu menu;
    private Map<String, Object> defaultOptions;

    public DefaultMenuResolver(final String menuName, final MenuFactory menuFactory) {
        this.menu = menuFactory.getMenu(menuName);
    }

    public void setDefaultOptions(final Map<String, Object> defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.menu, "menu must be not null");
        if (this.defaultOptions == null) {
            this.defaultOptions = new HashMap<>();
        }
    }

    @Override
    public Menu getFullMenu() {
        return this.menu;
    }

    @Override
    public Menu getAuthorizedMenu(final Authorization authorization) {
        final List<MenuItem> items = new ArrayList<>();
        for (final MenuItem item : this.menu.getItems()) {
            copyMatchedItemTo(item, authorization, items);
        }
        final Menu menu = new Menu(this.menu.getName());
        menu.getItems().addAll(items);
        return menu;
    }

    /**
     * 如果指定菜单项匹配指定权限集，则复制后加入指定菜单项集合中
     *
     * @param item
     *            菜单项
     * @param authorization
     *            授权集
     * @param items
     *            目标菜单项集合
     */
    private void copyMatchedItemTo(final MenuItem item, final Authorization authorization,
            final List<MenuItem> items) {
        // 如果profile不匹配，则直接忽略，也不再查找子菜单
        if (!item.isProfileFitted()) {
            return;
        }
        // 当前菜单授权不匹配，则不再检查子菜单项和操作
        if (!item.isContained(authorization)) {
            return;
        }
        // 检查子菜单项
        final List<MenuItem> newSubs = new ArrayList<>();
        for (final MenuItem sub : item.getSubs()) {
            copyMatchedItemTo(sub, authorization, newSubs);
        }
        // 构建新的菜单项对象，以免影响缓存的完整菜单对象的数据
        final MenuItem newItem = new MenuItem(item.getCaption(), item.getIcon(), item.getAction());
        newItem.getOptions().putAll(item.getOptions());
        newItem.getCaptions().putAll(item.getCaptions());
        newItem.getSubs().addAll(newSubs);

        items.add(newItem);
    }

    @Override
    public List<Authority> getAuthorites(final Map<String, Object> options) {
        if (options == null) {
            return new ArrayList<>();
        }
        final List<Authority> items = new ArrayList<>();
        for (final MenuItem item : this.menu.getItems()) {
            addMatchedAuthority(item, options, items);
        }
        return items;
    }

    /**
     * 添加指定菜单动作中匹配指定选项集的权限到指定授权集中
     *
     * @param action
     *            菜单动作
     * @param options
     *            选项集
     * @param authes
     *            目标菜单项集
     */
    private void addMatchedAuthority(final MenuItem action, final Map<String, Object> options,
            final List<Authority> authes) {
        if (isMatchedOptions(action, options)) {
            final Authority auth = action.getAuthority();
            if (auth != null && auth.isNotEmpty()) {
                authes.add(auth);
            }
        }
        if (action instanceof MenuItem) { // 如果菜单动作是菜单项，则需进一步添加包含的菜单操作和子菜单项中的匹配授权
            final MenuItem item = action;
            for (final MenuItem sub : item.getSubs()) {
                addMatchedAuthority(sub, options, authes);
            }
        }
    }

    /**
     * 判断指定选项映射集是否与指定菜单动作匹配
     *
     * @param action
     *            菜单动作
     * @param options
     *            选项映射集
     * @return 指定选项映射集是否与指定菜单动作匹配
     */
    private boolean isMatchedOptions(final MenuItem action, final Map<String, Object> options) {
        // 菜单动作中的选项集必须包含指定选项集中的所有选项，且选项值要相等
        // 遍历选项集中的每一个选项，只要有一个选项不匹配，则匹配失败
        for (final Entry<String, Object> entry : options.entrySet()) {
            final String key = entry.getKey();
            Object value = action.getOptions().get(key);
            if (value == null) { // 菜单动作的选项集中没有当前选项，则从默认选项集中取
                value = this.defaultOptions.get(key);
            }
            // 菜单动作中不包含某选项，或选项值不相等，则表示不匹配
            if (value == null) {
                return false;
            }
            if (!entry.getValue().equals(value)) {
                return false;
            }
        }
        return true;
    }

}
