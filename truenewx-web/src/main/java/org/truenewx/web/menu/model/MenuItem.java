package org.truenewx.web.menu.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.truenewx.core.tuple.Binary;
import org.truenewx.core.tuple.Binate;
import org.truenewx.web.http.HttpLink;

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

    public MenuItem(final String auth, final String caption, final String href, final String target,
                    final String icon) {
        super(auth, caption);
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
    public String getAuth(final String href, final HttpMethod method) {
        // 菜单项上的访问方法固定为GET方式
        if (this.link.isMatched(href, method)) {
            return getAuth();
        }
        String auth = super.getAuth(href, method);
        if (auth != null) {
            return auth;
        }
        for (final MenuOperation operation : this.operations) {
            auth = operation.getAuth(href, method);
            if (auth != null) {
                return auth;
            }
        }
        for (final MenuItem sub : this.subs) {
            auth = sub.getAuth(href, method);
            if (auth != null) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public String getAuth(final String beanId, final String methodName, final Integer argCount) {
        for (final MenuOperation operation : this.operations) {
            final String auth = operation.getAuth(beanId, methodName, argCount);
            if (auth != null) {
                return auth;
            }
        }
        for (final MenuItem sub : this.subs) {
            final String auth = sub.getAuth(beanId, methodName, argCount);
            if (auth != null) {
                return auth;
            }
        }
        return null;
    }

    public Set<String> getAllAuths() {
        final Set<String> result = new HashSet<>();
        String auth = getAuth();
        if (auth != null) {
            result.add(auth);
        }
        for (final MenuOperation operation : this.operations) {
            auth = operation.getAuth();
            if (auth != null) {
                result.add(auth);
            }
        }
        for (final MenuItem sub : this.subs) {
            result.addAll(sub.getAllAuths());
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
            if (sub.contains(href, method)) { // 直接下级即包含，则添加下标和动作对后返回
                final List<Binate<Integer, MenuAction>> indexes = new ArrayList<>();
                indexes.add(new Binary<Integer, MenuAction>(i, sub));
                return indexes;
            } else { // 否则尝试从更下级中查找
                final List<Binate<Integer, MenuAction>> indexes = sub.indexesOf(href, method);
                if (indexes.size() > 0) { // 在更下级中找到
                    indexes.add(0, new Binary<Integer, MenuAction>(i, sub));
                    return indexes;
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean contains(final String href, final HttpMethod method) {
        if (this.link.isMatched(href, method)) {
            return true;
        }
        if (super.contains(href, method)) {
            return true;
        }
        for (final MenuOperation operation : this.operations) {
            if (operation.contains(href, method)) {
                return true;
            }
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
        if (getAuth().equals(auth)) { // 如果当前菜单项匹配
            actions.add(this);
        }
        for (final MenuOperation operation : this.operations) {
            if (operation.getAuth().equals(auth)) { // 如果包含的特性匹配
                actions.add(operation);
            }
        }
        for (final MenuItem sub : this.subs) { // 加入所有子菜单项中的匹配动作
            actions.addAll(sub.getActions(auth));
        }
        return actions;
    }
}
