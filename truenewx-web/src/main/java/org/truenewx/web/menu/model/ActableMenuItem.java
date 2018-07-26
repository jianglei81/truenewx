package org.truenewx.web.menu.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.truenewx.core.tuple.Binary;
import org.truenewx.core.tuple.Binate;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;

import com.google.common.base.Predicate;

/**
 * 动作型菜单项
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ActableMenuItem extends AbstractActableMenuItem {

    public static final String TYPE = "actable";

    private static final long serialVersionUID = 4892116514076333340L;

    public ActableMenuItem(String caption, String icon, MenuItemAction action) {
        super(TYPE, caption, icon, action);
    }

    public String getCaption() {
        return getCaptions().get(Locale.getDefault());
    }

    public Authority getAuthority() {
        MenuItemAction action = getAction();
        return action == null ? null : action.getAuthority();
    }

    public String getPermission() {
        Authority authority = getAuthority();
        return authority == null ? null : authority.getPermission();
    }

    public List<MenuItem> getSubs(Predicate<MenuItem> predicate) {
        List<MenuItem> subs = getSubs();
        if (predicate == null) {
            return subs;
        }
        List<MenuItem> list = new ArrayList<>();
        for (MenuItem sub : subs) {
            if (predicate.apply(sub)) {
                if (sub instanceof ActableMenuItem) {
                    ActableMenuItem actableSub = (ActableMenuItem) sub;
                    ActableMenuItem newSub = actableSub.clone();
                    newSub.getSubs().addAll(actableSub.getSubs(predicate));
                    list.add(newSub);
                } else {
                    list.add(sub.clone());
                }
            }
        }
        return list;
    }

    @Override
    public ActableMenuItem clone() {
        ActableMenuItem item = new ActableMenuItem(getCaption(), getIcon(), getAction());
        item.getCaptions().putAll(getCaptions());
        item.getProfiles().addAll(getProfiles());
        item.getOptions().putAll(getOptions());
        return item;
    }

    public boolean contains(String href, HttpMethod method) {
        MenuItemAction action = getAction();
        return action != null && action.contains(href, method);
    }

    public boolean contains(String beanId, String methodName, Integer argCount) {
        MenuItemAction action = getAction();
        return action != null && action.contains(beanId, methodName, argCount);
    }

    public boolean isContained(Authorization authorization) {
        MenuItemAction action = getAction();
        // 当前菜单项没有设置动作，则视作无权限限制
        return action == null || action.isContained(authorization);
    }

    /**
     * 查找指定链接地址和链接方法匹配的权限
     *
     * @param href
     *            链接地址
     * @param method
     *            链接方法
     * @return 匹配的权限
     */
    public Authority findAuthority(String href, HttpMethod method) {
        if (contains(href, method)) {
            return getAuthority();
        }
        for (MenuItem sub : getSubs()) {
            if (sub instanceof ActableMenuItem) {
                Authority authority = ((ActableMenuItem) sub).findAuthority(href, method);
                if (authority != null) { // 找到一个即返回，后续即使匹配也无视
                    return authority;
                }
            }
        }
        return null;
    }

    public Authority findAuthority(String beanId, String methodName, Integer argCount) {
        if (contains(beanId, methodName, argCount)) {
            return getAuthority();
        }
        for (MenuItem sub : getSubs()) {
            if (sub instanceof ActableMenuItem) {
                Authority authority = ((ActableMenuItem) sub).findAuthority(beanId, methodName,
                        argCount);
                if (authority != null) {
                    return authority;
                }
            }
        }
        return null;
    }

    public Set<Authority> getAllAuthorities() {
        Set<Authority> result = new HashSet<>();
        Authority authority = getAuthority();
        if (authority != null) {
            result.add(authority);
        }
        for (MenuItem sub : getSubs()) {
            if (sub instanceof ActableMenuItem) {
                result.addAll(((ActableMenuItem) sub).getAllAuthorities());
            }
        }
        return result;
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
    public List<Binate<Integer, MenuItem>> indexesOf(String href, HttpMethod method) {
        List<MenuItem> subs = getSubs();
        for (int i = 0; i < subs.size(); i++) {
            MenuItem sub = subs.get(i);
            if (sub instanceof ActableMenuItem) {
                ActableMenuItem actableSub = (ActableMenuItem) sub;
                List<Binate<Integer, MenuItem>> indexes = actableSub.indexesOf(href, method);
                // 先在更下级中找
                if (indexes.size() > 0) { // 在更下级中找到
                    indexes.add(0, new Binary<>(i, sub)); // 加上对应的下级索引
                    return indexes;
                }
                // 更下级中没找到再到直接下级找，以免更下级中包含有与直接下级一样的链接
                if (actableSub.contains(href, method)) { // 直接下级中找到
                    indexes.add(new Binary<>(i, sub));
                    return indexes;
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Binate<Integer, MenuItem>> indexesOf(String beanId, String methodName,
            Integer argCount) {
        List<MenuItem> subs = getSubs();
        for (int i = 0; i < subs.size(); i++) {
            MenuItem sub = subs.get(i);
            if (sub instanceof ActableMenuItem) {
                ActableMenuItem actableSub = (ActableMenuItem) sub;
                // 先在更下级中找
                List<Binate<Integer, MenuItem>> indexes = actableSub.indexesOf(beanId, methodName,
                        argCount);
                if (indexes.size() > 0) { // 在更下级中找到
                    indexes.add(0, new Binary<>(i, sub)); // 加上对应的下级索引
                    return indexes;
                }
                // 更下级中没找到再到直接下级找，以免更下级中包含有与直接下级一样的链接
                if (actableSub.contains(beanId, methodName, argCount)) { // 直接下级中找到
                    indexes.add(new Binary<>(i, sub));
                    return indexes;
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return getCaption(); // 为了便于调试定位，将显示名称作为菜单项字符串形式输出
    }

}
