package org.truenewx.core.enums.support.functor;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import org.truenewx.core.enums.annotation.EnumValue;

import com.google.common.base.Enums;
import com.google.common.base.Function;

/**
 * 函数：获取枚举值
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class FuncEnumValue implements Function<Enum<?>, String> {
    /**
     * 单实例
     */
    public static final FuncEnumValue INSTANCE = new FuncEnumValue();

    private FuncEnumValue() {
    }

    @Override
    @Nullable
    public String apply(final Enum<?> enumConstant) {
        final Field field = Enums.getField(enumConstant);
        final EnumValue ev = field.getAnnotation(EnumValue.class);
        if (ev != null) {
            return ev.value();
        }
        return null;
    }

}
