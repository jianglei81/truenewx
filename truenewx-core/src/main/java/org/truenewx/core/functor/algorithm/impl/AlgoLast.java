package org.truenewx.core.functor.algorithm.impl;

import javax.annotation.Nullable;

import org.truenewx.core.functor.algorithm.Algorithm;

import com.google.common.base.Predicate;

/**
 * 取集合最后一个元素的算法
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class AlgoLast implements Algorithm {

    /**
     * 私有构造函数
     */
    private AlgoLast() {
    }

    /**
     * 获取指定可迭代集合中，满足指定断言条件的最后一个元素
     * 
     * @param iterable
     *            可迭代集合
     * @param predicate
     *            断言条件，为null时忽略
     * @return 满足条件的最后一个元素
     */
    @Nullable
    public static <T> T visit(@Nullable final Iterable<T> iterable,
                    @Nullable final Predicate<T> predicate) {
        T result = null;
        if (iterable != null) {
            if (predicate == null) {
                for (final T obj : iterable) {
                    result = obj;
                }
            } else {
                for (final T obj : iterable) {
                    if (predicate.apply(obj)) {
                        result = obj;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取指定数组中，满足指定断言条件的最后一个元素
     * 
     * @param array
     *            数组
     * @param predicate
     *            断言条件，为null时忽略
     * @return 满足条件的最后一个元素
     */
    @Nullable
    public static <T> T visit(@Nullable final T[] array, @Nullable final Predicate<T> predicate) {
        T result = null;
        if (array != null) {
            if (predicate == null) {
                for (final T obj : array) {
                    result = obj;
                }
            } else {
                for (final T obj : array) {
                    if (predicate.apply(obj)) {
                        result = obj;
                    }
                }
            }
        }
        return result;
    }
}
