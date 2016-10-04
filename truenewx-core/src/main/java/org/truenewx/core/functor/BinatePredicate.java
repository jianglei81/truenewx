package org.truenewx.core.functor;

import javax.annotation.Nullable;

import org.truenewx.core.tuple.Binate;

import com.google.common.base.Predicate;

/**
 * 二元参数断言
 * 
 * @author jianglei
 * @since JDK 1.8
 * @param <A1>
 *            第一个参数的类型
 * @param <A2>
 *            第二个参数的类型
 */
public abstract class BinatePredicate<A1, A2> implements Predicate<Binate<A1, A2>> {

    @Override
    public final boolean apply(@Nullable final Binate<A1, A2> input) {
        final A1 arg1;
        final A2 arg2;
        if (input != null) {
            arg1 = input.getLeft();
            arg2 = input.getRight();
        } else {
            arg1 = null;
            arg2 = null;
        }
        return apply(arg1, arg2);
    }

    public abstract boolean apply(A1 arg1, A2 arg2);

}
