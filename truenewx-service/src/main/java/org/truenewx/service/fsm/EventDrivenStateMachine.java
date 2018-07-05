package org.truenewx.service.fsm;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.event.EventRegistrar;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.UnitaryEntity;
import org.truenewx.data.user.UserIdentity;

/**
 * 事件驱动的有限状态机
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class EventDrivenStateMachine<U extends UnitaryEntity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity, E extends TransitEvent<I, K, T>>
        extends AbstractStateMachine<U, K, S, T, I> {

    @Autowired
    public void setEventRegistrar(final EventRegistrar eventRegistrar) {
        eventRegistrar.register(this);
    }

    /**
     * 响应事件，子类必须覆写并使用@Subscribe注解进行标注，否则无法响应事件
     *
     * @param event
     *            事件
     * @throws HandleableException
     *             如果处理过程出现异常
     */
    public void onEvent(final E event) throws HandleableException {
        transit(event.getUserIdentity(), event.getKey(), event.getTransition(), event.getContext());
    }

}
