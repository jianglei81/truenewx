package org.truenewx.data.orm.dao;

import java.io.Serializable;

import org.truenewx.data.finder.OwnedUnityFinder;
import org.truenewx.data.model.unity.OwnedUnity;

/**
 * 从属单体的DAO
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 * @param <O>
 *            所属者类型
 */
public interface OwnedUnityDao<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
                extends UnityDao<T, K>, OwnedUnityFinder<T, K, O> {
    /**
     * 获取指定所属者下的单体个数
     *
     * @param owner
     *            所属者
     * @return 指定所属者下的单体个数
     */
    int count(O owner);

    /**
     * 递增指定单体的指定数值属性值
     * 
     * @param owner
     *            所属者
     * @param id
     *            单体标识
     * @param propertyName
     *            数值属性名
     * @param step
     *            递增的值，为负值即表示递减
     * @return 单体
     */
    T increaseNumber(O owner, K id, String propertyName, Number step);
}
