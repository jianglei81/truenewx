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
 * @param <I>
 *            用户标识类型
 */
public interface TransitAction<U extends UnitaryEntity<K>, K extends Serializable, S extends Enum<S>, T extends Enum<T>, I extends UserIdentity> {
    /**
     * 获取转换枚举。每个转换动作都对应且仅对应一个转换枚举
     *
     * @return 转换枚举
     */
    T getTransition();

    /**
     *
     * @return 当前转换动作可能的起始状态集
     */
    S[] getBeginStates();

    /**
     * 获取在指定起始状态执行当前转换动作后的结束状态
     *
     * @param beginState
     *            起始状态
     * @param condition
     *            条件
     *
     * @return 结束状态，如果在指定起始状态下不能根据指定条件执行当前转换动作，则返回null
     */
    @Nullable
    S getEndState(S beginState, Object condition);

    /**
     * 指定用户对指定标识表示的实体，在指定上下文情况时，执行动作
     *
     * @param userIdentity
     *            用户标识
     * @param entity
     *            实体标识
     * @param context
     *            上下文
     * @return 动作是否正常执行
     *
     * @throws HandleableException
     *             如果执行过程中出现错误
     */
    boolean execute(I userIdentity, U entity, Object context) throws HandleableException;
}
