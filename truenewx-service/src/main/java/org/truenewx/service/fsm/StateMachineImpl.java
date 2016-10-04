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

import com.google.common.eventbus.Subscribe;

/**
 * 有限状态机实现
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <K>
 *            标识类型
 * @param <S>
 *            状态枚举类型
 * @param <T>
 *            转换枚举类型
 * @param <E>
 *            转换事件类型
 */
public class StateMachineImpl<K extends Serializable, S extends Enum<S>, T extends Enum<T>, E extends TransitEvent<K, T>>
                implements StateMachine<K, S, T, E> {
    /**
     * 起始状态
     */
    private S startState;

    private StateGetter<K, S> stateGetter;

    private Map<S, Map<T, TransitAction<K, S, T>>> stateTransitionActionMapping = new HashMap<>();

    public void setStartState(final S startState) {
        this.startState = startState;
    }

    public void setStateGetter(final StateGetter<K, S> stateGetter) {
        this.stateGetter = stateGetter;
    }

    public void setEventRegistrar(final EventRegistrar eventRegistrar) {
        eventRegistrar.register(this);
    }

    public void setTransitActions(
                    final Collection<? extends TransitAction<K, S, T>> transitActions) {
        for (final TransitAction<K, S, T> action : transitActions) {
            for (final S state : action.getStates()) {
                Map<T, TransitAction<K, S, T>> transitionActionMapping = this.stateTransitionActionMapping
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
        final Map<T, TransitAction<K, S, T>> transitionActionMapping = this.stateTransitionActionMapping
                        .get(state);
        if (transitionActionMapping != null) {
            transitions.addAll(transitionActionMapping.keySet());
        }
        return transitions;
    }

    private TransitAction<K, S, T> getTransitAction(final S state, final T transition) {
        if (state != null && transition != null) {
            final Map<T, TransitAction<K, S, T>> transitionActionMapping = this.stateTransitionActionMapping
                            .get(state);
            if (transitionActionMapping != null) {
                return transitionActionMapping.get(transition);
            }
        }
        return null;
    }

    @Override
    public S getNextState(final S state, final E event) {
        final TransitAction<K, S, T> action = getTransitAction(state, event.getTransition());
        if (action != null) {
            return action.getNextState(state, event.getContext());
        }
        return null;
    }

    @Override
    @Subscribe
    @WriteTransactional
    public void transit(final E event) throws HandleableException {
        final K key = event.getKey();
        final S state = this.stateGetter.getState(key);
        final TransitAction<K, S, T> action = getTransitAction(state, event.getTransition());
        if (action == null) {
            throw new UnsupportedTransitionException(state, event.getTransition());
        }
        action.execute(key, event.getContext());
    }
}
