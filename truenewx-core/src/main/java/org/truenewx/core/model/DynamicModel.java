package org.truenewx.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.core.util.BeanUtil;
import org.truenewx.core.util.JsonUtil;

/**
 * 动态模型（含有动态属性）
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DynamicModel implements PropertyDynamic {
    /**
     * 动态属性名值映射集
     */
    private Map<String, Object> properties;

    public DynamicModel() {
        this.properties = new HashMap<>();
    }

    public DynamicModel(final Map<String, ?> properties) {
        this.properties = new HashMap<>(properties);
    }

    @Override
    public void set(final String name, final Object value) {
        if (!BeanUtil.setPropertyValue(this, name, value)) { // 先尝试设置固定属性，再设置动态属性
            if (value == null) {
                this.properties.remove(name);
            } else {
                this.properties.put(name, value);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(final String name) {
        Object value = BeanUtil.getPropertyValue(this, name);
        // 先尝试获取固定属性，再获取动态属性
        if (value == null) {
            value = this.properties.get(name);
        }
        return (T) value;
    }

    @Override
    public void setAll(final Map<String, ?> map) {
        this.properties.clear();
        this.properties.putAll(map);
    }

    @Override
    public Map<String, Object> getAll() {
        final String[] excludedProperties = ArrayUtils.addAll(getExcludedProperties(), "all");
        final Map<String, Object> map = BeanUtil.toMap(this, excludedProperties);
        for (final Entry<String, Object> entry : this.properties.entrySet()) {
            final String name = entry.getKey();
            if (!map.containsKey(name) && !ArrayUtils.contains(excludedProperties, name)) {
                final Object value = entry.getValue();
                if (value != null) {
                    if (value instanceof CharSequence) {
                        if (((CharSequence) value).length() > 0) {
                            map.put(name, value);
                        }
                    } else {
                        map.put(name, value);
                    }
                }
            }
        }
        return map;
    }

    /**
     * 获取在{@link DynamicModel#getAll()}方法中要排除的属性名清单<br/>
     * 由子类覆写提供
     *
     * @return 在asMap()方法中要排除的属性名清单
     */
    protected String[] getExcludedProperties() {
        return null;
    }

    @Override
    public final synchronized void shift(final String oldName, final String newName,
            final boolean retain) {
        if (!oldName.equals(newName)) {
            set(newName, get(oldName));
            if (!retain) {
                set(oldName, null);
            }
        }
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(getAll());
    }

}
