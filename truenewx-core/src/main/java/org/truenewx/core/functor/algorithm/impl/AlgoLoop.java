package org.truenewx.core.functor.algorithm.impl;

import java.util.Iterator;

import org.truenewx.core.functor.BinatePredicate;
import org.truenewx.core.functor.algorithm.Algorithm;

/**
 * 算法：遍历
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class AlgoLoop implements Algorithm {
    /**
     * 遍历指定迭代器，每一步用指定断言判断是否继续下一步
     * 
     * @param iterator
     *            迭代器
     * @param predicate
     *            是否继续下一步的断言
     */
    public static <T> void visit(final Iterator<T> iterator,
                    final BinatePredicate<T, Integer> predicate) {
        int i = 0;
        while (iterator.hasNext()) {
            if (!predicate.apply(iterator.next(), i++)) {
                break;
            }
        }
    }
}
