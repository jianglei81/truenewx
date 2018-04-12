package org.truenewx.service.fsm;

import java.io.Serializable;
import java.util.Set;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.UnitaryEntity;

/**
 * 有限状态机
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
public interface StateMachine<U extends UnitaryEntity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, E extends TransitEvent<K, T>> {
    /**
     * 获取起始状态。有限状态机具有且仅具有一个起始状态
     *
     * @return 起始状态
     */
    S getStartState();

    /**
     * 获取指定状态下可进行的转换清单
     *
     * @param state
     *            状态
     * @return 可进行的转换清单
     */
    Set<T> getTransitions(S state);

    /**
     * 获取在指定状态下发生指定事件时将进入的下一个状态
     *
     * @param state
     *            状态
     * @param event
     *            事件
     * @return 下一个状态
     */
    S getNextState(S state, E event);

    /**
     * 发生指定事件进行转换
     *
     * @param event
     *            事件
     * @return 转换事件影响的实体
     * @throws HandleableException
     *             转换过程中出现异常
     */
    U transit(E event) throws HandleableException;

}
