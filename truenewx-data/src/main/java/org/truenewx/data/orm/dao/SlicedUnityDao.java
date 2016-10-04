package org.truenewx.data.orm.dao;

import java.io.Serializable;

import org.truenewx.data.finder.SlicedUnityFinder;
import org.truenewx.data.model.unity.SlicedUnity;

/**
 * 切分单体DAO
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
public interface SlicedUnityDao<T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable>
                extends SlicedDao<T, S>, SlicedUnityFinder<T, K, S> {
    /**
     * 递增指定单体的指定数值属性值
     *
     * @param slicer
     *            切分者
     * @param id
     *            单体标识
     * @param propertyName
     *            数值属性名
     * @param step
     *            递增的值，为负值即表示递减
     * @return 单体
     */
    T increaseNumber(S slicer, K id, String propertyName, Number step);
}
