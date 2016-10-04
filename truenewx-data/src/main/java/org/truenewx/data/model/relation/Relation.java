package org.truenewx.data.model.relation;

import java.io.Serializable;

import org.truenewx.data.model.Entity;

/**
 * 关系模型
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <L>
 *            左标识类型
 * @param <R>
 *            右标识类型
 */
public interface Relation<L extends Serializable, R extends Serializable>
                extends Entity, Serializable {

    /**
     *
     * @return 左标识
     */
    L getLeftId();

    /**
     *
     * @return 右标识
     */
    R getRightId();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);
}
