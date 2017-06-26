package org.truenewx.core.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.BeforeFilter;

/**
 * 附加类型字段的序列化过滤器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TypeSerializeFilter extends BeforeFilter {

    private Class<?>[] baseClasses;

    public TypeSerializeFilter(final Class<?>... baseClasses) {
        this.baseClasses = baseClasses;
    }

    private boolean isAppendable(final Class<?> type) {
        for (final Class<?> baseClass : this.baseClasses) {
            if (baseClass.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeBefore(final Object object) {
        if (object != null) {
            final Class<?> type = object.getClass();
            if (isAppendable(type)) {
                writeKeyValue(JSON.DEFAULT_TYPE_KEY, type.getName());
            }
        }
    }

}
