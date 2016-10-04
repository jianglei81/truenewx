package org.truenewx.core.functor;

import javax.annotation.Nullable;

import org.truenewx.core.tuple.Triple;

import com.google.common.base.Predicate;

/**
 * 三元参数断言
 * 
 * @author jianglei
 * @since JDK 1.8
 * @param <A1>
 *            第一个参数的类型
 * @param <A2>
 *            第二个参数的类型
 * @param <A3>
 *            第三个参数的类型
 */
public abstract class TriplePredicate<A1, A2, A3> implements Predicate<Triple<A1, A2, A3>> {

    @Override
    @Nullable
    public final boolean apply(@Nullable final Triple<A1, A2, A3> input) {
        final A1 arg1;
        final A2 arg2;
        final A3 arg3;
        if (input != null) {
            arg1 = input.getLeft();
            arg2 = input.getMiddle();
            arg3 = input.getRight();
        } else {
            arg1 = null;
            arg2 = null;
            arg3 = null;
        }
        return apply(arg1, arg2, arg3);
    }

    public abstract boolean apply(A1 arg1, A2 arg2, A3 arg3);

}
