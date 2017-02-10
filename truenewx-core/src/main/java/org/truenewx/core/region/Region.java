package org.truenewx.core.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

/**
 * 行政区划
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class Region {
    /**
     * 代号
     */
    private String code;
    /**
     * 显示名
     */
    private String caption;
    /**
     * 分组
     */
    private String group;
    /**
     * 父选项
     */
    private Region parent;
    /**
     * 子选项集
     */
    private Map<String, Region> subs;

    /**
     * @param code
     *            代号
     * @param caption
     *            显示名
     */
    public Region(final String code, final String caption) {
        this.code = code;
        this.caption = caption;
    }

    /**
     * @param code
     *            代号
     * @param caption
     *            显示名
     * @param group
     *            所属分组
     */
    public Region(final String code, final String caption, final String group) {
        this(code, caption);
        this.group = group;
    }

    protected void setCode(final String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    protected void setCaption(final String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return this.caption;
    }

    protected void setGroup(final String group) {
        this.group = group;
    }

    public String getGroup() {
        return this.group;
    }

    public Region getParent() {
        return this.parent;
    }

    public String getParentCode() {
        return this.parent == null ? null : this.parent.getCode();
    }

    public Map<String, Region> getSubs() {
        return this.subs;
    }

    public Collection<Region> getSubCollection() {
        return this.subs == null ? null : this.subs.values();
    }

    /**
     * @return 获取确保非null的子选项集
     */
    private Map<String, Region> getNonNullSubs() {
        if (this.subs == null) {
            this.subs = new LinkedHashMap<>();
        }
        return this.subs;
    }

    /**
     * 添加子选项
     *
     * @param sub
     *            子选项
     */
    public void addSub(final Region sub) {
        sub.parent = this;
        getNonNullSubs().put(sub.getCode(), sub);
    }

    /**
     * 获取代号为指定代号的子选项
     *
     * @param code
     *            代号
     * @return 匹配的子选项，如果没找到则返回null
     */
    @Nullable
    public Region getSubByCode(@Nullable final String code) {
        if (this.subs != null && StringUtils.isNotEmpty(code)) {
            return this.subs.get(code);
        }
        return null;
    }

    /**
     * 获取显示名为指定显示名的子选项
     *
     * @param caption
     *            显示名
     * @return 匹配的子选项，如果没找到则返回null
     */
    @Nullable
    public Region getSubByCaption(final String caption) {
        if (this.subs != null) {
            for (final Region sub : this.subs.values()) {
                if (StringUtils.equals(caption, sub.getCaption())) {
                    return sub;
                }
            }
        }
        return null;
    }

    /**
     * 判断是否包含子级项
     *
     * @return 是否包含子级项
     *
     * @author jianglei
     */
    public boolean isIncludingSub() {
        return this.subs != null && this.subs.size() > 0;
    }

    /**
     * 判断是否包含孙级项
     *
     * @return 是否包含孙级项
     *
     * @author jianglei
     */
    public boolean isIncludingGrandSub() {
        if (this.subs != null) {
            for (final Region sub : this.subs.values()) {
                if (sub.isIncludingSub()) {
                    // 只要有一个子级项有子级项，则说明有孙级
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取级别，国家级为0，省级为1，以此类推
     *
     * @return 级别
     */
    public int getLevel() {
        int level = 0;
        Region parent = getParent();
        while (parent != null) {
            level++;
            parent = parent.getParent();
        }
        return level;
    }

    public Region clone(final boolean subs) {
        final Region option = new Region(this.code, this.caption, this.group);
        option.parent = this.parent;
        if (subs) {
            option.subs = new LinkedHashMap<>(this.subs);
        }
        return option;
    }

    /**
     * 获取当前选项在所处选项树中从顶级选项到当前选项的选项路径，第0个元素为顶级选项，最后一个元素为当前选项自身
     *
     * @return 当前选项在所处选项树中从顶级选项到当前选项的选项路径
     */
    public List<Region> getLinkFromTop() {
        final List<Region> link = new ArrayList<>();
        Region option = this;
        link.add(option);
        while (option.getParent() != null) {
            option = option.getParent();
            link.add(option);
        }
        Collections.reverse(link);
        return link;
    }

}
