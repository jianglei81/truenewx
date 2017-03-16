package org.truenewx.web.rpc.server.functor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.truenewx.core.functor.BinatePredicate;

/**
 * 断言：等价的类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PredEquivalentClass extends BinatePredicate<Class<?>, Class<?>> {
    /**
     * 单例
     */
    public static final PredEquivalentClass INSTANCE = new PredEquivalentClass();

    /**
     * 等价的类型集
     */
    private Map<Class<?>, Class<?>> equivalents = new HashMap<Class<?>, Class<?>>();

    private PredEquivalentClass() {
        putEquivalent(boolean.class, Boolean.class);
        putEquivalent(byte.class, Byte.class);
        putEquivalent(char.class, Character.class);
        putEquivalent(short.class, Short.class);
        putEquivalent(int.class, Integer.class);
        putEquivalent(long.class, Long.class);
        putEquivalent(float.class, Float.class);
        putEquivalent(double.class, Double.class);
    }

    private void putEquivalent(final Class<?> clazz, final Class<?> otherClass) {
        this.equivalents.put(clazz, otherClass);
        this.equivalents.put(otherClass, clazz);
    }

    public Class<?> getEquivalentClass(final Class<?> clazz) {
        return this.equivalents.get(clazz);
    }

    @Override
    public boolean apply(final Class<?> expected, @Nullable final Class<?> actual) {
        if (actual == null) {
            return !expected.isPrimitive();
        }
        if (expected == actual || expected.isAssignableFrom(actual)) {
            return true;
        }
        // 当期望类型和实际类型中有一个为基本数据类型，而另一个不是基本数据类型时
        // 从等价类型集中获取匹配类型进行比较
        if (expected.isPrimitive() != actual.isPrimitive()
                && getEquivalentClass(expected) == actual) {
            return true;
        }
        // 如果期望类型为数组，则实际类型也必须为元素类型匹配的数组
        if (expected.isArray() && actual.isArray()) {
            return apply(expected.getComponentType(), actual.getComponentType());
        }
        return false;
    }

}
