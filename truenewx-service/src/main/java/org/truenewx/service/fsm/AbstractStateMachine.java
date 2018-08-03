package org.truenewx.service.fsm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.spring.transaction.annotation.WriteTransactional;
import org.truenewx.data.model.UnitaryEntity;
import org.truenewx.data.user.UserIdentity;
import org.truenewx.service.ServiceSupport;

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
        extends ServiceSupport implements StateMachine<U, K, S, T, I> {
    /**
     * 起始状态
     */
    private S startState;

    private List<TransitAction<U, K, S, T, I>> actions = new ArrayList<>();

    public void setStartState(S startState) {
        this.startState = startState;
    }

    public void setTransitActions(
            Collection<? extends TransitAction<U, K, S, T, I>> transitActions) {
        this.actions.addAll(transitActions);
    }

    @Override
    public S getStartState() {
        return this.startState;
    }

    @Override
    public Set<T> getTransitions(S state) {
        Set<T> transitions = new HashSet<>();
        for (TransitAction<U, K, S, T, I> action : this.actions) {
            if (ArrayUtils.contains(action.getBeginStates(), state)) {
                transitions.add(action.getTransition());
            }
        }
        return transitions;
    }

    @Override
    public S[] getBeginStates(T transition) {
        for (TransitAction<U, K, S, T, I> action : this.actions) {
            if (action.getTransition() == transition) {
                return action.getBeginStates();
            }
        }
        return null;
    }

    private TransitAction<U, K, S, T, I> getTransitAction(S state, T transition, Object condition) {
        for (TransitAction<U, K, S, T, I> action : this.actions) {
            if (action.getTransition() == transition
                    && action.getEndState(state, condition) != null) {
                return action;
            }
        }
        return null;
    }

    @Override
    public S getNextState(S state, T transition, Object condition) {
        TransitAction<U, K, S, T, I> action = getTransitAction(state, transition, condition);
        if (action != null) {
            return action.getEndState(state, condition);
        }
        return null;
    }

    @Override
    @WriteTransactional
    public U transit(I userIdentity, K key, T transition, Object context)
            throws HandleableException {
        U entity = loadEntity(userIdentity, key, context);
        S state = getState(entity);
        Object condition = getCondition(userIdentity, entity, context);
        TransitAction<U, K, S, T, I> action = getTransitAction(state, transition, condition);
        if (action == null) {
            throw new UnsupportedTransitionException(state, transition);
        }
        if (!action.execute(userIdentity, entity, context)) {
            return null;
        }
        return entity;
    }

    /**
     * 加载指定实体，需确保返回非空的实体，如果找不到指定实体，则需抛出业务异常
     *
     * @param userIdentity
     *            用户标识
     * @param key
     *            实体标识
     * @param context
     *            上下文
     * @return 实体
     * @throws BusinessException
     *             如果找不到实体
     */
    protected abstract U loadEntity(I userIdentity, K key, Object context) throws BusinessException;

    /**
     * 从指定实体中获取状态值。实体可能包含多个状态属性，故不通过让实体实现获取状态的接口来实现
     *
     * @param entity
     *            实体
     * @return 状态值
     */
    protected abstract S getState(U entity);

    /**
     * 获取转换条件，用于定位转换动作
     *
     * @param userIdentity
     *            用户标识
     * @param entity
     *            实体
     * @param context
     *            转换上下文
     * @return 转换条件
     */
    protected abstract Object getCondition(I userIdentity, U entity, Object context);

}
