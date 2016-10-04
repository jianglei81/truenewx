package org.truenewx.core.functor.algorithm.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.truenewx.core.Strings;
import org.truenewx.core.functor.algorithm.Algorithm;

import com.google.common.base.Defaults;

/**
 * 算法：获取指定类型的默认值
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class AlgoDefaultValue implements Algorithm {
    private static final Map<Class<?>, Object> DEFAULTS = new HashMap<Class<?>, Object>();

    static {
        put(String.class, Strings.EMPTY);
        put(Byte.class, Byte.valueOf((byte) 0));
        put(Character.class, Character.valueOf((char) 0));
        put(Short.class, Short.valueOf((short) 0));
        put(Integer.class, Integer.valueOf(0));
        put(Long.class, Long.valueOf(0l));
        put(Float.class, Float.valueOf(0.0f));
        put(Double.class, Double.valueOf(0.0d));
        put(Boolean.class, Boolean.FALSE);
        put(BigDecimal.class, BigDecimal.ZERO);
        put(BigInteger.class, BigInteger.ZERO);
        put(Currency.class, Currency.getInstance(Locale.getDefault()));
    }

    private static <T> void put(final Class<T> clazz, final T defaultValue) {
        DEFAULTS.put(clazz, defaultValue);
    }

    private AlgoDefaultValue() {
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T visit(final Class<T> clazz) {
        if (clazz.isEnum()) {
            return clazz.getEnumConstants()[0];
        } else if (Date.class.isAssignableFrom(clazz)) {
            return (T) new Date();
        } else {
            T result = Defaults.defaultValue(clazz);
            if (result == null) {
                result = (T) DEFAULTS.get(clazz);
            }
            if (result == null) {
                try {
                    result = clazz.newInstance();
                } catch (final Exception e) {
                }
            }
            return result;
        }
    }
}
