package org.truenewx.web.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.menu.model.MenuAction;
import org.truenewx.web.menu.model.MenuItem;
import org.truenewx.web.menu.model.MenuOperation;

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
        Assert.notNull(this.menu);
        if (this.defaultOptions == null) {
            this.defaultOptions = new HashMap<>();
        }
    }

    @Override
    public Menu getFullMenu() {
        return this.menu;
    }

    @Override
    public Menu getAuthorizedMenu(final String[] authorites) {
        final List<MenuItem> items = new ArrayList<>();
        for (final MenuItem item : this.menu.getItems()) {
            copyMatchedItemTo(item, authorites, items);
        }
        return new Menu(this.menu.getName(), items);
    }

    /**
     * 如果指定菜单项匹配指定权限集，则复制后加入指定菜单项集合中
     *
     * @param item
     *            菜单项
     * @param authorites
     *            权限集
     * @param items
     *            目标菜单项集合
     */
    private void copyMatchedItemTo(final MenuItem item, final String[] authorites,
            final List<MenuItem> items) {
        // 当前菜单项权限为空，则需要检查包含的子菜单项和操作中是否有匹配的，才决定是否加入目标集合中
        // 否则当前菜单项权限匹配时，才加入目标集合中
        final List<MenuItem> newSubs = new ArrayList<>();
        for (final MenuItem sub : item.getSubs()) {
            copyMatchedItemTo(sub, authorites, newSubs);
        }
        final List<MenuOperation> newOperations = new ArrayList<>();
        for (final MenuOperation operation : item.getOperations()) {
            if (ArrayUtils.contains(authorites, operation.getAuth())) {
                newOperations.add(operation);
            }
        }
        final String auth = item.getAuth();
        if ((StringUtils.isBlank(auth) || !ArrayUtils.contains(authorites, auth))
                && newSubs.isEmpty() && newOperations.isEmpty()) {
            return; // 当前菜单权限为空或权限不匹配，且不包含匹配的子菜单和操作，则忽略当前菜单项
        }
        final MenuItem newItem = new MenuItem(auth, item.getCaption(), item.getHref(),
                item.getTarget(), item.getIcon());
        newItem.getLinks().addAll(item.getLinks());
        newItem.getOptions().putAll(item.getOptions());
        newItem.getCaptions().putAll(item.getCaptions());
        newItem.getSubs().addAll(newSubs);
        newItem.getOperations().addAll(newOperations);

        items.add(newItem);
    }

    @Override
    public String[] getAuthorites(final Map<String, Object> options) {
        if (options == null) {
            return null;
        }
        final Set<String> authorities = new LinkedHashSet<>();
        for (final MenuItem item : this.menu.getItems()) {
            addMatchedAuthority(item, options, authorities);
        }
        return authorities.toArray(new String[authorities.size()]);
    }

    /**
     * 添加指定菜单动作中匹配指定选项集的权限到指定权限集中
     *
     * @param action
     *            菜单动作
     * @param options
     *            选项集
     * @param authorities
     *            目标权限集
     */
    private void addMatchedAuthority(final MenuAction action, final Map<String, Object> options,
            final Set<String> authorities) {
        if (isMatchedOptions(action, options)) {
            final String auth = action.getAuth();
            if (StringUtils.isNotBlank(auth)) {
                authorities.add(auth);
            }
        }
        if (action instanceof MenuItem) { // 如果菜单动作是菜单项，则需进一步添加包含的菜单操作和子菜单项中的匹配权限
            final MenuItem item = (MenuItem) action;
            for (final MenuOperation operation : item.getOperations()) {
                addMatchedAuthority(operation, options, authorities);
            }
            for (final MenuItem sub : item.getSubs()) {
                addMatchedAuthority(sub, options, authorities);
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
    private boolean isMatchedOptions(final MenuAction action, final Map<String, Object> options) {
        // 遍历选项集中的每一个选项，只要有一个选项不匹配，则匹配失败
        for (final Entry<String, Object> entry : options.entrySet()) {
            final String key = entry.getKey();
            Object value = action.getOptions().get(key);
            if (value == null) { // 菜单动作的选项集中没有当前选项，则从默认选项集中取
                value = this.defaultOptions.get(key);
            }
            if (!entry.getValue().equals(value)) {
                return false;
            }
        }
        return true;
    }

}
