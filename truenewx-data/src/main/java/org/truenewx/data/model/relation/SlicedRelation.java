package org.truenewx.data.model.relation;

import java.io.Serializable;

import org.truenewx.data.model.SlicedEntity;

/**
 * 切分关系
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <L>
 *            左标识类型
 * @param <R>
 *            右标识类型
 * @param <S>
 *            切分者类型
 */
public interface SlicedRelation<L extends Serializable, R extends Serializable, S extends Serializable>
                extends Relation<L, R>, SlicedEntity<S> {

}
