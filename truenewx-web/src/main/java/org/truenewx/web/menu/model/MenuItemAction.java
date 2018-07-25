package org.truenewx.web.menu.model;

import java.io.Serializable;

import org.springframework.http.HttpMethod;
import org.truenewx.web.http.HttpLink;
import org.truenewx.web.http.HttpResource;
import org.truenewx.web.rpc.RpcPort;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;

/**
 * 菜单项动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuItemAction extends AbstractMenuItemAction implements Serializable {

    private static final long serialVersionUID = 1915212755458650260L;

    public MenuItemAction(final Authority authority, final HttpLink link) {
        super(authority, link);
    }

    /**
     * 相当于获取 auth.role
     *
     * @return 所需角色
     */
    public String getRole() {
        return getAuthority().getRole();
    }

    /**
     * 相当于获取 auth.permission
     *
     * @return 所需权限
     */
    public String getPermission() {
        return getAuthority().getPermission();
    }

    public HttpLink getLink() {
        for (final HttpResource res : getResources()) {
            // 第一个链接为默认链接
            if (res instanceof HttpLink) {
                return (HttpLink) res;
            }
        }
        return null;
    }

    public boolean contains(final String href, final HttpMethod method) {
        return getResources().stream().filter(resource -> resource instanceof HttpLink)
                .anyMatch(resource -> ((HttpLink) resource).matches(href, method));
    }

    public boolean contains(final String beanId, final String methodName, final Integer argCount) {
        return getResources().stream().filter(resource -> resource instanceof RpcPort)
                .anyMatch(resource -> ((RpcPort) resource).matches(beanId, methodName, argCount));
    }

    public boolean isContained(final Authorization authorization) {
        // 如果当前动作未指定授权，表示没有授权限制，视为匹配
        return getAuthority() == null || getAuthority().isContained(authorization);
    }

    @Override
    public String toString() {
        return getLink() + "(" + getAuthority() + ")";
    }

}
