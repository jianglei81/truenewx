package org.truenewx.data.finder;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.truenewx.data.model.unity.OwnedUnity;

/**
 * 具有所属者的单体的查找器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            单体标识类型
 * @param <O>
 *            所有者类型
 */
public interface OwnedUnityFinder<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
                extends UnityFinder<T, K> {
    /**
     * 根据所属者和标识获取单体
     *
     * @param owner
     *            所属者
     * @param id
     *            标识
     * @return 单体，如果没有匹配的标识，则返回null
     */
    @Nullable
    T find(O owner, K id);
}
