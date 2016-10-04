package org.truenewx.web.menu;

import java.util.Collection;

import org.truenewx.web.menu.model.Menu;

/**
 * 菜单工厂代理
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuFactoryProxy implements MenuFactory {

    private Collection<MenuFactory> targets;

    public void setTargets(final Collection<MenuFactory> targets) {
        this.targets = targets;
    }

    @Override
    public Menu getMenu(final String name) {
        if (this.targets != null) {
            for (final MenuFactory factory : this.targets) {
                final Menu menu = factory.getMenu(name);
                if (menu != null) {
                    return menu;
                }
            }
        }
        return null;
    }

}
