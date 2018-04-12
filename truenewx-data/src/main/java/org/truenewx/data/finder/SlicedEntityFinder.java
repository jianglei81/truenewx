package org.truenewx.data.finder;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.truenewx.data.model.SlicedEntity;

/**
 * 切分实体查找器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            切分实体类型
 * @param <K>
 *            标识类型
 * @param <S>
 *            切分者类型
 */
public interface SlicedEntityFinder<T extends SlicedEntity<S>, K extends Serializable, S extends Serializable>
        extends EntityFinder<T> {
    /**
     * 根据所属者和标识获取切分实体
     *
     * @param slicer
     *            切分者
     * @param id
     *            标识
     * @return 切分实体，如果没有匹配的标识，则返回null
     */
    @Nullable
    T find(S slicer, K key);
}
