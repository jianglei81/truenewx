package org.truenewx.data.finder;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.truenewx.data.model.unity.Unity;

/**
 * 单体查找器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            单体标识类型
 */
public interface UnityFinder<T extends Unity<K>, K extends Serializable>
        extends UnitaryEntityFinder<T, K> {
    /**
     * 根据标识获取单体
     *
     * @param id
     *            标识
     * @return 单体，如果没有匹配的标识，则返回null
     */
    @Override
    @Nullable
    T find(K id);
}
