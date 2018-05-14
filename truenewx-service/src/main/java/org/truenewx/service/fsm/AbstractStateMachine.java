package org.truenewx.service.fsm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.spring.transaction.annotation.WriteTransactional;
import org.truenewx.data.finder.UnitaryEntityFinder;
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

    private List<TransitAction<U, K, S, T, I>> actions = new ArrayList<>();

    public void setStartState(final S startState) {
        this.startState = startState;
    }

    public void setTransitActions(
            final Collection<? extends TransitAction<U, K, S, T, I>> transitActions) {
        this.actions.addAll(transitActions);
    }

    @Override
    public S getStartState() {
        return this.startState;
    }

    @Override
    public Set<T> getTransitions(final S state) {
        final Set<T> transitions = new HashSet<>();
        for (final TransitAction<U, K, S, T, I> action : this.actions) {
            if (ArrayUtils.contains(action.getBeginStates(), state)) {
                transitions.add(action.getTransition());
            }
        }
        return transitions;
    }

    private TransitAction<U, K, S, T, I> getTransitAction(final S state, final T transition,
            final Object condition) {
        for (final TransitAction<U, K, S, T, I> action : this.actions) {
            if (action.getTransition() == action && action.getEndState(state, condition) != null) {
                return action;
            }
        }
        return null;
    }

    @Override
    public S getNextState(final S state, final T transition, final Object condition) {
        final TransitAction<U, K, S, T, I> action = getTransitAction(state, transition, condition);
        if (action != null) {
            return action.getEndState(state, condition);
        }
        return null;
    }

    @Override
    @WriteTransactional
    public U transit(final I userIdentity, final K key, final T transition, final Object context)
            throws HandleableException {
        final U entity = getFinder().find(key);
        if (entity != null) {
            final S state = getState(entity);
            final Object condition = getCondition(userIdentity, entity, transition, context);
            final TransitAction<U, K, S, T, I> action = getTransitAction(state, transition,
                    condition);
            if (action == null) {
                throw new UnsupportedTransitionException(state, transition);
            }
            if (!action.execute(userIdentity, entity, context)) {
                return null;
            }
        }
        return entity;
    }

    protected abstract UnitaryEntityFinder<U, K> getFinder();

    protected abstract S getState(U entity);

    protected abstract Object getCondition(I userIdentity, final U entity, T transition,
            final Object context);

}
