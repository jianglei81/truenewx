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

}
