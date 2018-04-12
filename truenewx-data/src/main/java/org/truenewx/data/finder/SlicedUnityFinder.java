package org.truenewx.data.finder;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.truenewx.data.model.unity.SlicedUnity;

/**
 * 切分单体查找器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 * @param <S>
 *            切分者类型
 */
public interface SlicedUnityFinder<T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable>
        extends SlicedEntityFinder<T, K, S> {
    /**
     * 根据所属者和标识获取单体
     *
     * @param slicer
     *            切分者
     * @param id
     *            标识
     * @return 单体，如果没有匹配的标识，则返回null
     */
    @Override
    @Nullable
    T find(S slicer, K id);
}
