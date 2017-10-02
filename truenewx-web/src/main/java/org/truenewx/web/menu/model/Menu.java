package org.truenewx.web.menu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.truenewx.core.tuple.Binary;
import org.truenewx.core.tuple.Binate;
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
     * 菜单项集合
     */
    private List<MenuItem> items = new ArrayList<>();

    /**
     * 菜单构造函数
     *
     * @param name
     *            菜单名称
     */
    public Menu(final String name) {
        this.name = name;
    }

    /**
     * 菜单构造函数
     *
     * @param name
     *            菜单名称
     * @param items
     *            菜单项集合
     */
    public Menu(final String name, final List<MenuItem> items) {
        this.name = name;
        this.items = items;
    }

    /**
     * @return 名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return 菜单项集合
     */
    public Iterable<MenuItem> getItems() {
        return this.items;
    }

    /**
     * 获取指定菜单项
     *
     * @param index
     *            菜单项索引下标
     * @return 菜单项
     */
    public MenuItem getItem(final int index) {
        return this.items.get(index);
    }

    public void addItem(final MenuItem item) {
        this.items.add(item);
    }

    public Authority getAuthority(final String href, final HttpMethod method) {
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
        for (final MenuItem item : this.items) {
            final Authority authority = item.findAuthority(beanId, methodName, argCount);
            if (authority != null) {
                return authority;
            }
        }
        return null;
    }

    public Authority[] getAllAuthorities() {
        final Set<Authority> authorities = new HashSet<>();
        for (final MenuItem item : this.items) {
            authorities.addAll(item.getAllAuthorities());
        }
        return authorities.toArray(new Authority[authorities.size()]);
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
    public List<Binate<Integer, MenuItem>> indexesOf(final String href, final HttpMethod method) {
        if (StringUtils.isBlank(href)) {
            return null;
        }
        for (int i = 0; i < this.items.size(); i++) {
            final MenuItem item = this.items.get(i);
            // 先在更下级中找
            final List<Binate<Integer, MenuItem>> indexes = item.indexesOf(href, method);
            if (indexes.size() > 0) { // 在下级中找到
                indexes.add(0, new Binary<Integer, MenuItem>(i, item)); // 加上对应的下级索引
                return indexes;
            }
            // 更下级中没找到再到直接下级找，以免更下级中包含有与直接下级一样的链接
            if (item.contains(href, method)) { // 在当前级别找到
                indexes.add(new Binary<Integer, MenuItem>(i, item));
                return indexes;
            }
        }
        return new ArrayList<>();
    }

    public List<Binate<Integer, MenuItem>> indexesOf(final String beanId, final String methodName,
            final Integer argCount) {
        for (int i = 0; i < this.items.size(); i++) {
            final MenuItem item = this.items.get(i);
            // 先在更下级中找
            final List<Binate<Integer, MenuItem>> indexes = item.indexesOf(beanId, methodName,
                    argCount);
            if (indexes.size() > 0) { // 在下级中找到
                indexes.add(0, new Binary<Integer, MenuItem>(i, item)); // 加上对应的下级索引
                return indexes;
            }
        }
        return new ArrayList<>();
    }

}
