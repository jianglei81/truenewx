package org.truenewx.core.functor.algorithm.impl;

import java.util.Iterator;

import org.truenewx.core.functor.BinatePredicate;
import org.truenewx.core.functor.algorithm.Algorithm;

/**
 * 算法：从集合中移除元素
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class AlgoRemove implements Algorithm {

    /**
     * 从迭代器中移除符合指定断言的元素
     * 
     * @param iterator
     *            迭代器
     * @param predicate
     *            移除断言
     * 
     * @author jianglei
     */
    public static <T> void visit(final Iterator<T> iterator,
                    final BinatePredicate<T, Integer> predicate) {
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.apply(iterator.next(), i++)) {
                iterator.remove();
            }
        }
    }

    /**
     * 从迭代集合中移除符合指定断言的元素
     * 
     * @param iterable
     *            迭代集合
     * @param predicate
     *            移除断言
     * 
     * @author jianglei
     */
    public static <T> void visit(final Iterable<T> iterable,
                    final BinatePredicate<T, Integer> predicate) {
        final Iterator<T> iterator = iterable.iterator();
        visit(iterator, predicate);
    }
}
