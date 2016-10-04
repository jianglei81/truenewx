package org.truenewx.data.orm.dao;

import java.io.Serializable;

import org.truenewx.data.finder.SlicedRelationFinder;
import org.truenewx.data.model.relation.SlicedRelation;

/**
 * 切分关系DAO
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SlicedRelationDao<T extends SlicedRelation<L, R, S>, L extends Serializable, R extends Serializable, S extends Serializable>
                extends SlicedDao<T, S>, SlicedRelationFinder<T, L, R, S> {

}
