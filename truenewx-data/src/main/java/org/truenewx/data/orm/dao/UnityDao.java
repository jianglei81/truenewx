package org.truenewx.data.orm.dao;

import java.io.Serializable;

import org.truenewx.data.finder.UnityFinder;
import org.truenewx.data.model.unity.Unity;

/**
 * 单体Dao
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            单体标识类型
 */
public interface UnityDao<T extends Unity<K>, K extends Serializable>
                extends Dao<T>, UnityFinder<T, K> {

    /**
     * 递增指定单体的指定数值属性值
     *
     * @param id
     *            单体标识
     * @param propertyName
     *            数值属性名
     * @param step
     *            递增的值，为负值即表示递减
     * @return 单体
     */
    T increaseNumber(K id, String propertyName, Number step);

}
