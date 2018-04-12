package org.truenewx.service.fsm;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.truenewx.core.event.EventRegistrar;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.spring.transaction.annotation.WriteTransactional;
import org.truenewx.data.model.UnitaryEntity;

import com.google.common.eventbus.Subscribe;

/**
 * 抽象的有限状态机
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <U>
 *            实体类型
 * @param <K>
 *            标识类型
 * @param <S>
 *            状态枚举类型
 * @param <T>
 *            转换枚举类型
 * @param <E>
 *            转换事件类型
 */
public abstract class AbstractStateMachine<U extends UnitaryEntity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, E extends TransitEvent<K, T>>
        implements StateMachine<U, K, S, T, E> {
    /**
     * 起始状态
     */
    private S startState;

    private Map<S, Map<T, TransitAction<U, K, S, T>>> stateTransitionActionMapping = new HashMap<>();

    public void setStartState(final S startState) {
        this.startState = startState;
    }

    public void setEventRegistrar(final EventRegistrar eventRegistrar) {
        eventRegistrar.register(this);
    }

    public void setTransitActions(
            final Collection<? extends TransitAction<U, K, S, T>> transitActions) {
        for (final TransitAction<U, K, S, T> action : transitActions) {
            for (final S state : action.getStates()) {
                Map<T, TransitAction<U, K, S, T>> transitionActionMapping = this.stateTransitionActionMapping
                        .get(state);
                if (transitionActionMapping == null) {
                    transitionActionMapping = new HashMap<>();
                    this.stateTransitionActionMapping.put(state, transitionActionMapping);
                }
                transitionActionMapping.put(action.getTransition(), action);
            }
        }
    }

    @Override
    public S getStartState() {
        return this.startState;
    }

    @Override
    public Set<T> getTransitions(final S state) {
        final Set<T> transitions = new HashSet<>();
        final Map<T, TransitAction<U, K, S, T>> transitionActionMapping = this.stateTransitionActionMapping
                .get(state);
        if (transitionActionMapping != null) {
            transitions.addAll(transitionActionMapping.keySet());
        }
        return transitions;
    }

    private TransitAction<U, K, S, T> getTransitAction(final S state, final T transition) {
        if (state != null && transition != null) {
            final Map<T, TransitAction<U, K, S, T>> transitionActionMapping = this.stateTransitionActionMapping
                    .get(state);
            if (transitionActionMapping != null) {
                return transitionActionMapping.get(transition);
            }
        }
        return null;
    }

    @Override
    public S getNextState(final S state, final E event) {
        final TransitAction<U, K, S, T> action = getTransitAction(state, event.getTransition());
        if (action != null) {
            return action.getNextState(state, event.getContext());
        }
        return null;
    }

    @Override
    @Subscribe
    @WriteTransactional
    public U transit(final E event) throws HandleableException {
        final K key = event.getKey();
        final S state = getState(key);
        final TransitAction<U, K, S, T> action = getTransitAction(state, event.getTransition());
        if (action == null) {
            throw new UnsupportedTransitionException(state, event.getTransition());
        }
        return action.execute(key, event.getContext());
    }

    protected abstract S getState(K key);

}
