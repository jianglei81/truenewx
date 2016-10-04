package org.truenewx.data.orm.dao;

import java.io.Serializable;

import org.truenewx.data.finder.OwnedSlicedUnityFinder;
import org.truenewx.data.model.unity.OwnedSlicedUnity;

/**
 * 具有所属者的切分单体DAO
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
public interface OwnedSlicedUnityDao<T extends OwnedSlicedUnity<K, S, O>, K extends Serializable, S extends Serializable, O extends Serializable>
                extends SlicedUnityDao<T, K, S>, OwnedSlicedUnityFinder<T, K, S, O> {
    /**
     * 获取指定切分者下指定所属者的单体个数
     *
     * @param slicer
     *            切分者
     * @param owner
     *            所属者
     * @return 单体个数
     */
    int count(S slicer, O owner);
}
