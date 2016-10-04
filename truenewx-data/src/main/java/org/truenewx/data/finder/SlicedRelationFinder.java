package org.truenewx.data.finder;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.truenewx.data.model.relation.Relation;

/**
 * 切分关系查找器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            关系类型
 * @param <L>
 *            左标识类型
 * @param <R>
 *            右标识类型
 * @param <S>
 *            切分者类型
 */
public interface SlicedRelationFinder<T extends Relation<L, R>, L extends Serializable, R extends Serializable, S extends Serializable>
        extends EntityFinder<T> {
    /**
     * 根据标识获取关系
     *
     * @param slicer
     *            切分者
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @return 关系，如果没有匹配的标识，则返回null
     */
    @Nullable
    T find(S slicer, L leftId, R rightId);

    /**
     * 判断是否存在指定标识表示的关系
     * 
     * @param slicer
     *            切分者
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @return 是否存在指定标识表示的关系
     */
    boolean exists(S slicer, L leftId, R rightId);
}
