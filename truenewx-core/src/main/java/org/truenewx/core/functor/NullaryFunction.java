package org.truenewx.core.functor;

import javax.annotation.Nullable;

import com.google.common.base.Function;

/**
 * 无参函数
 * 
 * @author jianglei
 * @since JDK 1.8
 * @param <R>
 *            结果类型
 */
public abstract class NullaryFunction<R> implements Function<Void, R> {

    @Override
    @Nullable
    public final R apply(final Void input) {
        return apply();
    }

    public abstract R apply();

}
