package org.truenewx.web.menu;

import java.util.Map;

import org.truenewx.web.menu.model.Menu;

/**
 * 菜单解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface MenuResolver {

    /**
     * 获取完整菜单
     *
     * @return 完整菜单
     */
    public Menu getFullMenu();

    /**
     * 获取仅包含指定权限集动作的菜单
     *
     * @param authorites
     *            权限集
     * @return 仅包含指定权限集动作的菜单
     */
    public Menu getAuthorizedMenu(String[] authorites);

    /**
     * 获取指定选项映射集限定的权限集
     *
     * @return 指定选项映射集限定的权限集
     */
    public String[] getAuthorites(Map<String, Object> options);

}
