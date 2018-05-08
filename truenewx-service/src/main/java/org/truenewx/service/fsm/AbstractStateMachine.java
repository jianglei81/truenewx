package org.truenewx.service.fsm;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.EnumUtils;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.spring.transaction.annotation.WriteTransactional;
import org.truenewx.data.model.UnitaryEntity;
import org.truenewx.data.user.UserIdentity;

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
public abstract class AbstractStateMachine<U extends UnitaryEntity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity>
        implements StateMachine<U, K, S, T, I> {
    /**
     * 起始状态
     */
    private S startState;

    private Map<S, Map<T, TransitAction<U, K, S, T>>> stateTransitionActionMapping = new HashMap<>();

    public void setStartState(final S startState) {
        this.startState = startState;
    }

    public void setTransitActions(
            final Collection<? extends TransitAction<U, K, S, T>> transitActions) {
        @SuppressWarnings("unchecked")
        final List<S> states = EnumUtils.getEnumList(this.startState.getClass());
        for (final S state : states) {
            this.stateTransitionActionMapping.put(state,
                    getTransitableActions(transitActions, state));
        }
    }

    private Map<T, TransitAction<U, K, S, T>> getTransitableActions(
            final Collection<? extends TransitAction<U, K, S, T>> actions, final S state) {
        final Map<T, TransitAction<U, K, S, T>> result = new HashMap<>();
        for (final TransitAction<U, K, S, T> action : actions) {
            if (action.getNextState(null, state) != null) {
                result.put(action.getTransition(), action);
            }
        }
        return result;
    }

    @Override
    public S getStartState() {
        return this.startState;
    }

    @Override
    public Set<T> getTransitions(final UserIdentity userIdentity, final S state) {
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
    public S getNextState(final UserIdentity userIdentity, final S state, final T transition) {
        final TransitAction<U, K, S, T> action = getTransitAction(state, transition);
        if (action != null) {
            return action.getNextState(userIdentity, state);
        }
        return null;
    }

    @Override
    @WriteTransactional
    public U transit(final UserIdentity userIdentity, final K key, final T transition,
            final Object context) throws HandleableException {
        final S state = getState(key);
        final TransitAction<U, K, S, T> action = getTransitAction(state, transition);
        if (action == null) {
            throw new UnsupportedTransitionException(state, transition);
        }
        return action.execute(null, key, context);
    }

    protected abstract S getState(K key);

}
