package org.truenewx.service.fsm;

import java.io.Serializable;

/**
 * 状态获取器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <K>
 *            标识类型
 * @param <S>
 *            状态枚举类型
 */
public interface StateGetter<K extends Serializable, S extends Enum<S>> {

    /**
     * 获取指定标识表示的实体的状态
     *
     * @param key
     *            标识
     * @return 状态
     */
    S getState(K key);

}
