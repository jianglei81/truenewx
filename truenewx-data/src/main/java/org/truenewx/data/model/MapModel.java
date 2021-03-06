package org.truenewx.data.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.functor.impl.FuncHashCode;
import org.truenewx.core.functor.impl.PredEqual;

/**
 * 内含映射集的模型
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class MapModel implements ValueModel {
    private Map<String, Object> values = new HashMap<>();

    public Map<String, Object> getValues() {
        return this.values;
    }

    public void setValues(final Map<String, Object> values) {
        if (values == null) {
            this.values.clear();
        } else {
            this.values = values;
        }
    }

    /**
     * @return 除空字符串值外的所有值
     */
    public Map<String, Object> getValuesExceptEmpty() {
        final Map<String, Object> result = new HashMap<>();
        for (final Entry<String, Object> entry : this.values.entrySet()) {
            final Object value = entry.getValue();
            if (!(value instanceof String) || StringUtils.isNotEmpty((String) value)) {
                result.put(entry.getKey(), value);
            }
        }
        return result;
    }

    /**
     * 转换指定项的数据类型
     * 
     * @param key
     *            项键
     * @param targetClass
     *            目标数据类型
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void convert(final String key, final Class<?> targetClass) {
        Object value = this.values.get(key);
        if (value != null) {
            if (value instanceof String) { // 字符串值才能转换
                final String s = (String) value;
                if (targetClass == Boolean.class) {
                    if (StringUtils.isEmpty(s)) {
                        this.values.remove(key);
                    } else {
                        this.values.put(key, Boolean.valueOf(s));
                    }
                } else if (targetClass.isEnum()) {
                    value = EnumUtils.getEnum((Class<Enum>) targetClass, s);
                    if (value != null) {
                        this.values.put(key, value);
                    } else {
                        this.values.remove(key);
                    }
                } else if (targetClass == Integer.class) {
                    this.values.put(key, Integer.valueOf(s));
                }
            } else if (targetClass == String.class) {
                this.values.put(key, value.toString());
            }
        }
    }

    @Override
    public int hashCode() {
        return FuncHashCode.INSTANCE.apply(this.values);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MapModel other = (MapModel) obj;
        return PredEqual.INSTANCE.apply(this.values, other.values);
    }

    @Override
    public String toString() {
        return this.values.toString();
    }
}
