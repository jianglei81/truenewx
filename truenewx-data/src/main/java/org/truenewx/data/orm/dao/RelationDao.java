package org.truenewx.data.orm.dao;

import java.io.Serializable;

import org.truenewx.data.finder.RelationFinder;
import org.truenewx.data.model.relation.Relation;

/**
 * 关系DAO
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            关系类型
 * @param <L>
 *            左标识类型
 * @param <R>
 *            右标识类型
 */
public interface RelationDao<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends Dao<T>, RelationFinder<T, L, R> {
    /**
     * 递增指定关系的指定数值属性值
     *
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @param propertyName
     *            数值属性名
     * @param step
     *            递增的值，为负值即表示递减
     * @return 关系
     */
    T increaseNumber(L leftId, R rightId, String propertyName, Number step);
}
