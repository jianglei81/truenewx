package org.truenewx.web.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.truenewx.web.menu.model.ActableMenuItem;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.menu.model.MenuItem;
import org.truenewx.web.menu.model.MenuItemAction;
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

    public DefaultMenuResolver(String menuName, MenuFactory menuFactory) {
        this.menu = menuFactory.getMenu(menuName);
    }

    public void setDefaultOptions(Map<String, Object> defaultOptions) {
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
    public Menu getAuthorizedMenu(Authorization authorization) {
        List<MenuItem> items = new ArrayList<>();
        for (MenuItem item : this.menu.getItems()) {
            copyMatchedItemTo(item, authorization, items);
        }
        Menu menu = new Menu(this.menu.getName());
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
    private void copyMatchedItemTo(MenuItem item, Authorization authorization,
            List<MenuItem> items) {
        // 如果profile不匹配，则直接忽略，也不再查找子菜单
        if (!item.isProfileFitted()) {
            return;
        }
        if (!(item instanceof ActableMenuItem)) { // 非动作型菜单项，直接视为匹配，加入
            items.add(item.clone());
            return;
        }
        // 动作型菜单项需进行更复杂的权限匹配校验
        ActableMenuItem actableItem = (ActableMenuItem) item;
        // 当前菜单授权不匹配，则不再检查子菜单项和操作
        if (!actableItem.isContained(authorization)) {
            return;
        }
        // 检查子菜单项
        List<MenuItem> newSubs = new ArrayList<>();
        for (MenuItem sub : actableItem.getSubs()) {
            copyMatchedItemTo(sub, authorization, newSubs);
        }
        MenuItemAction action = actableItem.getAction();
        // 当前菜单项配置有匹配的授权，或者子菜单项中有匹配的，才加入结果集中
        if (action != null || newSubs.size() > 0) {
            // 构建新的菜单项对象，以免影响缓存的完整菜单对象的数据
            ActableMenuItem newActableItem = actableItem.clone();
            newActableItem.getSubs().addAll(newSubs);
            items.add(newActableItem);
        }
    }

    @Override
    public List<Authority> getAuthorites(Map<String, Object> options) {
        if (options == null) {
            return new ArrayList<>();
        }
        List<Authority> items = new ArrayList<>();
        for (MenuItem item : this.menu.getItems()) {
            addMatchedAuthority(item, options, items);
        }
        return items;
    }

    /**
     * 添加指定菜单动作中匹配指定选项集的权限到指定授权集中
     *
     * @param item
     *            菜单动作
     * @param options
     *            选项集
     * @param authorities
     *            目标菜单项集
     */
    private void addMatchedAuthority(MenuItem item, Map<String, Object> options,
            List<Authority> authorities) {
        if (item instanceof ActableMenuItem) {
            ActableMenuItem actableItem = (ActableMenuItem) item;
            if (isMatchedOptions(item, options)) {
                Authority auth = actableItem.getAuthority();
                if (auth != null && auth.isNotEmpty()) {
                    authorities.add(auth);
                }
            }
            for (MenuItem sub : actableItem.getSubs()) {
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
    private boolean isMatchedOptions(MenuItem action, Map<String, Object> options) {
        // 菜单动作中的选项集必须包含指定选项集中的所有选项，且选项值要相等
        // 遍历选项集中的每一个选项，只要有一个选项不匹配，则匹配失败
        for (Entry<String, Object> entry : options.entrySet()) {
            String key = entry.getKey();
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
