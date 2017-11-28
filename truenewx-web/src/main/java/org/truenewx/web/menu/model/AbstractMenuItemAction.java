package org.truenewx.web.menu.model;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.web.http.HttpLink;
import org.truenewx.web.http.HttpResource;
import org.truenewx.web.security.authority.Authority;

/**
 * 抽象的菜单项动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
abstract class AbstractMenuItemAction {

    /**
     * 所需授权
     */
    private Authority authority;
    /**
     * 链接集合
     */
    private List<HttpResource> resources = new ArrayList<>();

    public AbstractMenuItemAction(final Authority authority, final HttpLink link) {
        this.authority = authority;
        if (link != null) {
            this.resources.add(link);
        }
    }

    /**
     *
     * @return 所需授权
     */
    public Authority getAuthority() {
        return this.authority;
    }

    public List<HttpResource> getResources() {
        return this.resources;
    }

}
