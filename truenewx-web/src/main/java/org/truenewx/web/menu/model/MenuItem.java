package org.truenewx.web.menu.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.truenewx.core.tuple.Binary;
import org.truenewx.core.tuple.Binate;
import org.truenewx.web.http.HttpLink;
import org.truenewx.web.security.authority.Authority;

/**
 * 菜单项类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuItem extends MenuAction {

    private static final long serialVersionUID = -6145127565332857618L;

    /**
     * 链接地址
     */
    private HttpLink link;

    /**
     * 链接目标
     */
    private String target;

    /**
     * 图标
     */
    private String icon;

    /**
     * 子项集合
     */
    private List<MenuItem> subs = new ArrayList<>();

    /**
     * 菜单操作集合
     */
    private List<MenuOperation> operations = new ArrayList<>();

    public MenuItem(final Authority authority, final String caption, final String href,
            final String target, final String icon) {
        super(authority, caption);
        this.link = new HttpLink(href);
        this.target = target;
        this.icon = icon;
    }

    /**
     * @return 菜单操作集合
     */
    public List<MenuOperation> getOperations() {
        return this.operations;
    }

    /**
     * @return 子项集合
     */
    public List<MenuItem> getSubs() {
        return this.subs;
    }

    /**
     *
     * @return 链接地址
     */
    public String getHref() {
        return this.link.getHref();
    }

    /**
     *
     * @return 链接类型
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * 图标
     */
    public String getIcon() {
        return this.icon;
    }

    /**
     * 获取指定链接地址和链接方法匹配的权限
     *
     * @param href
     *            链接地址
     * @param method
     *            链接方法
     * @return 匹配的权限
     */
    @Override
    public Authority getAuthority(final String href, final HttpMethod method) {
        // 菜单项上的访问方法固定为GET方式
        if (this.link.isMatched(href, method)) {
            return getAuthority();
        }
        Authority auth = super.getAuthority(href, method);
        if (auth != null) {
            return auth;
        }
        for (final MenuOperation operation : this.operations) {
            auth = operation.getAuthority(href, method);
            if (auth != null) {
                return auth;
            }
        }
        for (final MenuItem sub : this.subs) {
            auth = sub.getAuthority(href, method);
            if (auth != null) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public Authority getAuthority(final String beanId, final String methodName,
            final Integer argCount) {
        for (final MenuOperation operation : this.operations) {
            final Authority auth = operation.getAuthority(beanId, methodName, argCount);
            if (auth != null) {
                return auth;
            }
        }
        for (final MenuItem sub : this.subs) {
            final Authority auth = sub.getAuthority(beanId, methodName, argCount);
            if (auth != null) {
                return auth;
            }
        }
        return null;
    }

    public Set<String> getAllAuthorities() {
        final Set<String> result = new HashSet<>();
        String auth = getPermission();
        if (auth != null) {
            result.add(auth);
        }
        for (final MenuOperation operation : this.operations) {
            auth = operation.getPermission();
            if (auth != null) {
                result.add(auth);
            }
        }
        for (final MenuItem sub : this.subs) {
            result.addAll(sub.getAllAuthorities());
        }
        return result;
    }

    /**
     * 获取匹配指定RPC的菜单操作集合，包括各级子菜单中的菜单操作
     *
     * @param beanId
     *            Bean Id
     * @param methodName
     *            方法名
     * @param argCount
     *            参数个数，为null时忽略参数个数比较
     * @return 匹配指定RPC的菜单操作集合
     */
    public List<MenuOperation> getOperations(final String beanId, final String methodName,
            final Integer argCount) {
        final List<MenuOperation> operations = new ArrayList<>();
        for (final MenuOperation operation : this.operations) {
            if (operation.contains(beanId, methodName, argCount)) {
                operations.add(operation);
            }
        }
        for (final MenuItem sub : this.subs) {
            operations.addAll(sub.getOperations(beanId, methodName, argCount));
        }
        return operations;
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
    public List<Binate<Integer, MenuAction>> indexesOf(final String href, final HttpMethod method) {
        for (int i = 0; i < this.subs.size(); i++) {
            final MenuItem sub = this.subs.get(i);
            final List<Binate<Integer, MenuAction>> indexes = sub.indexesOf(href, method);
            // 先在更下级中找
            if (indexes.size() > 0) { // 在更下级中找到
                indexes.add(0, new Binary<Integer, MenuAction>(i, sub)); // 加上对应的下级索引
                return indexes;
            }
            // 更下级中没找到再到直接下级找，以免更下级中包含有与直接下级一样的链接
            if (sub.contains(href, method)) { // 直接下级中找到
                indexes.add(new Binary<Integer, MenuAction>(i, sub));
                return indexes;
            }
        }
        // 没有下级则在包含的操作中查找
        final List<Binate<Integer, MenuAction>> indexes = new ArrayList<>();
        for (int i = 0; i < this.operations.size(); i++) {
            final MenuOperation operation = this.operations.get(i);
            if (operation.contains(href, method)) {
                indexes.add(new Binary<Integer, MenuAction>(i, operation));
            }
        }
        return indexes;
    }

    @Override
    public boolean contains(final String href, final HttpMethod method) {
        if (this.link.isMatched(href, method)) {
            return true;
        }
        if (super.contains(href, method)) {
            return true;
        }
        return false;
    }

    /**
     * 获取指定权限的菜单动作集合
     *
     * @param auth
     *            权限名称
     * @return 指定权限的菜单动作集合
     */
    public List<MenuAction> getActions(final String auth) {
        final List<MenuAction> actions = new ArrayList<>();
        if (getPermission().equals(auth)) { // 如果当前菜单项匹配
            actions.add(this);
        }
        for (final MenuOperation operation : this.operations) {
            if (operation.getPermission().equals(auth)) { // 如果包含的特性匹配
                actions.add(operation);
            }
        }
        for (final MenuItem sub : this.subs) { // 加入所有子菜单项中的匹配动作
            actions.addAll(sub.getActions(auth));
        }
        return actions;
    }
}
