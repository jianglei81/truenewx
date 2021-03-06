package org.truenewx.service.fsm;

import java.io.Serializable;
import java.util.Set;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.UnitaryEntity;
import org.truenewx.data.user.UserIdentity;
import org.truenewx.service.Service;

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
 * @param <I>
 *            用户标识类型
 */
public interface StateMachine<U extends UnitaryEntity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity>
        extends Service {
    /**
     * 获取起始状态。有限状态机具有且仅具有一个起始状态
     *
     * @return 起始状态
     */
    S getStartState();

    /**
     * 获取在指定状态下可进行的转换清单
     *
     * @param state
     *            状态
     *
     * @return 可进行的转换清单
     */
    Set<T> getTransitions(S state);

    /**
     * 获取可进行指定转换的开始状态清单
     *
     * @param transition
     *            转换
     * @return 可进行指定转换的开始状态清单
     */
    S[] getBeginStates(T transition);

    /**
     * 获取在指定状态下，在指定条件下，进行指定转换后将进入的下一个状态
     *
     * @param state
     *            状态
     * @param transition
     *            转换
     * @param condition
     *            条件
     *
     * @return 下一个状态
     */
    S getNextState(S state, T transition, Object condition);

    /**
     * 指定用户对指定实体进行指定转换
     *
     * @param userIdentity
     *            用户标识
     * @param key
     *            实体标识
     * @param transition
     *            转换
     * @param context
     *            上下文
     *
     * @return 转换影响的实体
     * @throws HandleableException
     *             转换过程中出现异常
     */
    U transit(I userIdentity, K key, T transition, Object context) throws HandleableException;

}
