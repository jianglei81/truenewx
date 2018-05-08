package org.truenewx.service.fsm;

import java.io.Serializable;

import org.truenewx.core.event.ContextEvent;
import org.truenewx.data.user.UserIdentity;

/**
 * 状态转换事件
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <K>
 *            标识类型
 * @param <T>
 *            转换枚举类型
 */
public class TransitEvent<I extends UserIdentity, K extends Serializable, T extends Enum<T>>
        implements ContextEvent<Object> {
    private I userIdentity;
    private K key;
    private T transition;
    private Object context;

    public TransitEvent(final I userIdentity, final K key, final T transition,
            final Object context) {
        this.userIdentity = userIdentity;
        this.key = key;
        this.transition = transition;
        this.context = context;
    }

    public TransitEvent(final I userIdentity, final K key, final T transition) {
        this(userIdentity, key, transition, null);
    }

    public I getUserIdentity() {
        return this.userIdentity;
    }

    public K getKey() {
        return this.key;
    }

    public T getTransition() {
        return this.transition;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getContext() {
        return (C) this.context;
    }

}
