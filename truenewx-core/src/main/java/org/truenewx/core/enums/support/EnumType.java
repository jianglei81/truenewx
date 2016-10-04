package org.truenewx.core.enums.support;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.truenewx.core.model.Named;

/**
 * 枚举类型
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumType implements Named {
    /**
     * 名称
     */
    private String name;
    /**
     * 子名称
     */
    private String subname;
    /**
     * 说明
     */
    private String caption;
    /**
     * 枚举项映射集
     */
    private Map<String, EnumItem> items = new LinkedHashMap<String, EnumItem>();

    public EnumType(final String name, final String caption) {
        this.name = name;
        this.caption = caption;
    }

    public EnumType(final String name, final String subname, final String caption) {
        this.name = name;
        this.subname = subname;
        this.caption = caption;
    }

    void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getSubname() {
        return this.subname;
    }

    public String getCaption() {
        return this.caption;
    }

    public void addItem(final EnumItem item) {
        this.items.put(item.getKey(), item);
    }

    public EnumItem getItem(final String key, final String... keys) {
        EnumItem item = this.items.get(key);
        if (item != null && keys.length > 0) {
            final String[] subkeys = new String[keys.length - 1];
            if (subkeys.length > 0) {
                System.arraycopy(keys, 1, subkeys, 0, subkeys.length);
                item = item.getChild(keys[0], subkeys);
            }
        }
        return item;
    }

    /**
     * 获取所有直接枚举项
     * 
     * @return 所有直接枚举项
     */
    public Collection<EnumItem> getItems() {
        return this.items.values();
    }

    /**
     * 设置直接枚举项集
     * 
     * @param items
     *            直接枚举项集
     */
    public void setItems(final Collection<EnumItem> items) {
        synchronized (this.items) {
            this.items.clear();
            for (final EnumItem item : items) {
                addItem(item);
            }
        }
    }

    public EnumItem getItemByCaption(final String caption) {
        for (final EnumItem item : this.items.values()) {
            if (item.getCaption().equals(caption)) {
                return item;
            }
        }
        return null;
    }
}
