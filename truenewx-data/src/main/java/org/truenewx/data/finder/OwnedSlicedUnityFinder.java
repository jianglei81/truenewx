package org.truenewx.data.finder;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.truenewx.data.model.unity.OwnedSlicedUnity;

/**
 * 从属切分单体的查找器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 * @param <S>
 *            切分者类型
 * @param <O>
 *            所属者类型
 */
public interface OwnedSlicedUnityFinder<T extends OwnedSlicedUnity<K, S, O>, K extends Serializable, S extends Serializable, O extends Serializable>
                extends SlicedUnityFinder<T, K, S> {
    /**
     * 根据切分者、所属者和标识获取单体
     *
     * @param slicer
     *            切分者
     * @param owner
     *            所属者
     * @param id
     *            标识
     * @return 单体
     */
    @Nullable
    T find(S slicer, O owner, K id);
}
