package org.truenewx.service.fsm;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.UnitaryEntity;
import org.truenewx.data.user.UserIdentity;

/**
 * 转换动作
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
 */
public interface TransitAction<U extends UnitaryEntity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>> {
    /**
     * 获取转换枚举。每个转换动作都对应且仅对应一个转换枚举
     *
     * @return 转换枚举
     */
    T getTransition();

    /**
     * 获取指定用户在指定状态下进行当前转换动作将进入的下一个状态
     *
     * @param userIdentity
     *            用户标识，为null表示不考虑用户限定的情况
     * @param state
     *            状态。如果该状态不被本转换动作支持，则将返回null
     *
     * @return 下一个状态
     */
    @Nullable
    S getNextState(UserIdentity userIdentity, S state);

    /**
     * 指定用户对指定标识表示的实体，在指定上下文情况时，执行动作
     *
     * @param userIdentity
     *            用户标识
     * @param key
     *            实体标识
     * @param context
     *            上下文
     *
     * @return 影响的实体
     * @throws HandleableException
     *             如果执行过程中出现错误
     */
    U execute(UserIdentity userIdentity, K key, Object context) throws HandleableException;
}
