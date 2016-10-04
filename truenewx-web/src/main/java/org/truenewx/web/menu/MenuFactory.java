package org.truenewx.web.menu;

import org.truenewx.web.menu.model.Menu;

/**
 * 菜单工厂
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface MenuFactory {

    /**
     * 根据菜单名获取整个菜单
     *
     * @param name
     *            菜单名
     * @return 菜单
     */
    Menu getMenu(String name);

}
