package org.truenewx.web.security.authority;

import org.truenewx.web.menu.model.Menu;

/**
 * 带有菜单的授权信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuAuthorizationInfo extends DefaultAuthorizationInfo {

    private Menu menu;

    public MenuAuthorizationInfo(final boolean caching) {
        super(caching);
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void setMenu(final Menu menu) {
        this.menu = menu;
    }

}
