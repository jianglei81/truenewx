package org.truenewx.web.menu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.truenewx.core.tuple.Binary;
import org.truenewx.core.tuple.Binate;
import org.truenewx.web.http.HttpLink;
import org.truenewx.web.http.HttpResource;
import org.truenewx.web.rpc.RpcPort;
import org.truenewx.web.security.authority.Authority;

/**
 * 菜单类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class Menu implements Serializable {

    private static final long serialVersionUID = 7864620719633440806L;
    /**
     * 菜单名称
     */
    private String name;
    /**
     * 匿名可访问的资源集
     */
    private List<HttpResource> anonymousResources = new ArrayList<>();
    /**
     * 登录即可访问的资源集
     */
    private List<HttpResource> loginedResources = new ArrayList<>();
    /**
     * 获得授权才可访问的菜单项集
     */
    private List<MenuItem> items = new ArrayList<>();

    public Menu(final String name) {
        this.name = name;
    }

    /**
     * @return 名称
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return 匿名可访问的资源集
     */
    public List<HttpResource> getAnonymousResources() {
        return this.anonymousResources;
    }

    /**
     *
     * @return 登录即可访问的资源集
     */
    public List<HttpResource> getLoginedResources() {
        return this.loginedResources;
    }

    /**
     * @return 获得授权才可访问的菜单项集
     */
    public List<MenuItem> getItems() {
        return this.items;
    }

    /**
     * 判断指定链接是否可匿名访问
     */
    public boolean isAnonymous(final String href, final HttpMethod method) {
        for (final HttpResource res : this.anonymousResources) {
            if (res instanceof HttpLink) {
                final HttpLink link = (HttpLink) res;
                if (link.matches(href, method)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断指定RPC请求是否可匿名访问
     */
    public boolean isAnonymous(final String beanId, final String methodName,
            final Integer argCount) {
        for (final HttpResource res : this.anonymousResources) {
            if (res instanceof RpcPort) {
                final RpcPort rpc = (RpcPort) res;
                if (rpc.matches(beanId, methodName, argCount)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Authority getAuthority(final String href, final HttpMethod method) {
        // 登录即可访问资源匹配查找
        for (final HttpResource res : this.loginedResources) {
            if (res instanceof HttpLink) {
                final HttpLink link = (HttpLink) res;
                if (link.matches(href, method)) {
                    return Authority.LOGINED;
                }
            }
        }
        // 菜单项权限匹配查找
        for (final MenuItem item : this.items) {
            final Authority authority = item.findAuthority(href, method);
            if (authority != null) {
                return authority;
            }
        }
        return null;
    }

    public Authority getAuthority(final String beanId, final String methodName,
            final Integer argCount) {
        // 登录即可访问资源匹配查找
        for (final HttpResource res : this.loginedResources) {
            if (res instanceof RpcPort) {
                final RpcPort rpc = (RpcPort) res;
                if (rpc.matches(beanId, methodName, argCount)) {
                    return Authority.LOGINED;
                }
            }
        }
        // 菜单项权限匹配查找
        for (final MenuItem item : this.items) {
            final Authority authority = item.findAuthority(beanId, methodName, argCount);
            if (authority != null) {
                return authority;
            }
        }
        return null;
    }

    /**
     * 获取匹配指定链接地址和链接方法的菜单动作下标和对象集合
     *
     * @param href
     *            链接地址
     * @param method
     *            链接方法
     * @return 匹配指定链接地址和链接方法的菜单动作下标和对象集合
     */
    public List<Binate<Integer, MenuItem>> indexesOfItems(final String href,
            final HttpMethod method) {
        if (StringUtils.isBlank(href)) {
            return null;
        }
        for (int i = 0; i < this.items.size(); i++) {
            final MenuItem item = this.items.get(i);
            // 先在更下级中找
            final List<Binate<Integer, MenuItem>> indexes = item.indexesOf(href, method);
            if (indexes.size() > 0) { // 在下级中找到
                indexes.add(0, new Binary<>(i, item)); // 加上对应的下级索引
                return indexes;
            }
            // 更下级中没找到再到直接下级找，以免更下级中包含有与直接下级一样的链接
            if (item.contains(href, method)) { // 在当前级别找到
                indexes.add(new Binary<>(i, item));
                return indexes;
            }
        }
        return new ArrayList<>();
    }

    public List<Binate<Integer, MenuItem>> indexesOfItems(final String beanId,
            final String methodName, final Integer argCount) {
        for (int i = 0; i < this.items.size(); i++) {
            final MenuItem item = this.items.get(i);
            // 先在更下级中找
            final List<Binate<Integer, MenuItem>> indexes = item.indexesOf(beanId, methodName,
                    argCount);
            if (indexes.size() > 0) { // 在下级中找到
                indexes.add(0, new Binary<>(i, item)); // 加上对应的下级索引
                return indexes;
            }
            // 更下级中没找到再到直接下级找，以免更下级中包含有与直接下级一样的RPC
            if (item.contains(beanId, methodName, argCount)) { // 在当前级别找到
                indexes.add(new Binary<>(i, item));
                return indexes;
            }
        }
        return new ArrayList<>();
    }

}
