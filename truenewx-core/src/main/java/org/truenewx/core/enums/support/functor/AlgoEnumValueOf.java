package org.truenewx.core.enums.support.functor;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import org.truenewx.core.enums.annotation.EnumValue;
import org.truenewx.core.functor.algorithm.Algorithm;

import com.google.common.base.Enums;

/**
 * 算法：获取枚举值对应的枚举常量
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class AlgoEnumValueOf implements Algorithm {

    private AlgoEnumValueOf() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T visit(final Class<T> enumClass, @Nullable final String value) {
        if (value != null) {
            final Object[] enumConstants = enumClass.getEnumConstants();
            if (enumConstants != null) {
                for (final Enum<?> enumConstant : (Enum<?>[]) enumConstants) {
                    final Field field = Enums.getField(enumConstant);
                    final EnumValue ev = field.getAnnotation(EnumValue.class);
                    if (ev != null && value.trim().equals(ev.value())) {
                        return (T) enumConstant;
                    }
                }
            }
        }
        return null;
    }
}
