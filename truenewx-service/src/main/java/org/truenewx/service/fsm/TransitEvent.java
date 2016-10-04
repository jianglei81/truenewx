package org.truenewx.service.fsm;

import java.io.Serializable;

import org.truenewx.core.event.ContextEvent;

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
public class TransitEvent<K extends Serializable, T extends Enum<T>>
                implements ContextEvent<Object> {
    private K key;
    private T transition;
    private Object context;

    public TransitEvent(final K key, final T transition, final Object context) {
        this.key = key;
        this.transition = transition;
        this.context = context;
    }

    public TransitEvent(final K key, final T transition) {
        this(key, transition, null);
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
