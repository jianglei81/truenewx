package org.truenewx.web.menu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.truenewx.core.spring.core.env.functor.FuncProfile;
import org.truenewx.core.tuple.Binary;
import org.truenewx.core.tuple.Binate;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;

import com.google.common.base.Predicate;

/**
 * 菜单项类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuItem extends AbstractMenuItem implements Serializable {

    private static final long serialVersionUID = -6145127565332857618L;

    public MenuItem(final String caption, final String icon, final MenuItemAction action) {
        super(caption, icon, action);
    }

    public boolean isProfileFitted() {
        final String profile = FuncProfile.INSTANCE.apply();
        return StringUtils.isBlank(profile) || getProfiles().isEmpty()
                || getProfiles().contains(profile);
    }

    public String getCaption() {
        return getCaptions().get(Locale.getDefault());
    }

    public Authority getAuthority() {
        final MenuItemAction action = getAction();
        return action == null ? null : action.getAuthority();
    }

    public String getPermission() {
        final Authority authority = getAuthority();
        return authority == null ? null : authority.getPermission();
    }

    public List<MenuItem> getSubs(final Predicate<MenuItem> predicate) {
        final List<MenuItem> subs = getSubs();
        if (predicate == null) {
            return subs;
        }
        final List<MenuItem> list = new ArrayList<>();
        for (final MenuItem sub : subs) {
            if (predicate.apply(sub)) {
                final MenuItem newSub = sub.cloneWithoutSubs();
                newSub.getSubs().addAll(sub.getSubs(predicate));
                list.add(newSub);
            }
        }
        return list;
    }

    MenuItem cloneWithoutSubs() {
        final MenuItem item = new MenuItem(getCaption(), getIcon(), getAction());
        item.getCaptions().putAll(getCaptions());
        item.getProfiles().addAll(getProfiles());
        item.getOptions().putAll(getOptions());
        return item;
    }

    public boolean contains(final String href, final HttpMethod method) {
        final MenuItemAction action = getAction();
        return action != null && action.contains(href, method);
    }

    public boolean contains(final String beanId, final String methodName, final Integer argCount) {
        final MenuItemAction action = getAction();
        return action != null && action.contains(beanId, methodName, argCount);
    }

    public boolean isContained(final Authorization authorization) {
        final MenuItemAction action = getAction();
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
    public Authority findAuthority(final String href, final HttpMethod method) {
        if (contains(href, method)) {
            return getAuthority();
        }
        for (final MenuItem sub : getSubs()) {
            final Authority authority = sub.findAuthority(href, method);
            if (authority != null) { // 找到一个即返回，后续即使匹配也无视
                return authority;
            }
        }
        return null;
    }

    public Authority findAuthority(final String beanId, final String methodName,
            final Integer argCount) {
        if (contains(beanId, methodName, argCount)) {
            return getAuthority();
        }
        for (final MenuItem sub : getSubs()) {
            final Authority authority = sub.findAuthority(beanId, methodName, argCount);
            if (authority != null) {
                return authority;
            }
        }
        return null;
    }

    public Set<Authority> getAllAuthorities() {
        final Set<Authority> result = new HashSet<>();
        final Authority authority = getAuthority();
        if (authority != null) {
            result.add(authority);
        }
        for (final MenuItem sub : getSubs()) {
            result.addAll(sub.getAllAuthorities());
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
    public List<Binate<Integer, MenuItem>> indexesOf(final String href, final HttpMethod method) {
        final List<MenuItem> subs = getSubs();
        for (int i = 0; i < subs.size(); i++) {
            final MenuItem sub = subs.get(i);
            final List<Binate<Integer, MenuItem>> indexes = sub.indexesOf(href, method);
            // 先在更下级中找
            if (indexes.size() > 0) { // 在更下级中找到
                indexes.add(0, new Binary<>(i, sub)); // 加上对应的下级索引
                return indexes;
            }
            // 更下级中没找到再到直接下级找，以免更下级中包含有与直接下级一样的链接
            if (sub.contains(href, method)) { // 直接下级中找到
                indexes.add(new Binary<>(i, sub));
                return indexes;
            }
        }
        return new ArrayList<>();
    }

    public List<Binate<Integer, MenuItem>> indexesOf(final String beanId, final String methodName,
            final Integer argCount) {
        final List<MenuItem> subs = getSubs();
        for (int i = 0; i < subs.size(); i++) {
            final MenuItem sub = subs.get(i);
            // 先在更下级中找
            final List<Binate<Integer, MenuItem>> indexes = sub.indexesOf(beanId, methodName,
                    argCount);
            if (indexes.size() > 0) { // 在更下级中找到
                indexes.add(0, new Binary<>(i, sub)); // 加上对应的下级索引
                return indexes;
            }
            // 更下级中没找到再到直接下级找，以免更下级中包含有与直接下级一样的链接
            if (sub.contains(beanId, methodName, argCount)) { // 直接下级中找到
                indexes.add(new Binary<>(i, sub));
                return indexes;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return getCaption(); // 为了便于调试定位，将显示名称作为菜单项字符串形式输出
    }
}
