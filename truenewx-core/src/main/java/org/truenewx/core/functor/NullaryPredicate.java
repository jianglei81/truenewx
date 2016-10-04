package org.truenewx.core.functor;

import com.google.common.base.Predicate;

/**
 * 无参断言
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class NullaryPredicate implements Predicate<Void> {

    @Override
    public final boolean apply(final Void input) {
        return apply();
    }

    public abstract boolean apply();

}
