package org.truenewx.core.functor.algorithm.impl;

import java.util.Iterator;

import javax.annotation.Nullable;

import org.truenewx.core.functor.algorithm.Algorithm;

import com.google.common.base.Predicate;

/**
 * 算法：取集合中满足断言的第一条记录
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class AlgoFirst implements Algorithm {

    private AlgoFirst() {
    }

    /**
     * 取指定集合中满足指定断言条件的第一条记录
     * 
     * @param iterable
     *            集合
     * @param predicate
     *            断言，为null时忽略
     * @return 第一条记录
     */
    @Nullable
    public static <T> T visit(@Nullable final Iterable<T> iterable,
                    @Nullable final Predicate<T> predicate) {
        if (iterable != null) {
            if (predicate == null) {
                final Iterator<T> iterator = iterable.iterator();
                if (iterator.hasNext()) {
                    return iterator.next();
                }
            } else {
                for (final T object : iterable) {
                    if (predicate.apply(object)) {
                        return object;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 取指定数组中满足指定断言条件的第一条记录
     * 
     * @param array
     *            数组
     * @param predicate
     *            断言，为null时忽略
     * @return 第一条记录
     */
    public static @Nullable <T> T visit(final T[] array, @Nullable final Predicate<T> predicate) {
        if (array != null && array.length > 0) {
            if (predicate == null) {
                return array[0];
            } else {
                for (final T object : array) {
                    if (predicate.apply(object)) {
                        return object;
                    }
                }
            }
        }
        return null;
    }
}
