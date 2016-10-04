package org.truenewx.core.functor;

import javax.annotation.Nullable;

import org.truenewx.core.tuple.Binate;

import com.google.common.base.Function;

/**
 * 二元参数函数
 * 
 * @author jianglei
 * @since JDK 1.8
 * @param <A1>
 *            第一个参数的类型
 * @param <A2>
 *            第二个参数的类型
 * @param <R>
 *            结果类型
 */
public abstract class BinateFunction<A1, A2, R> implements Function<Binate<A1, A2>, R> {

    @Override
    @Nullable
    public final R apply(@Nullable final Binate<A1, A2> input) {
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

    public abstract R apply(A1 arg1, A2 arg2);

}
