package org.truenewx.data.finder;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.truenewx.data.model.UnitaryEntity;

/**
 * 单一标识实体的查找器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单一标识实体类型
 * @param <K>
 *            标识类型
 */
public interface UnitaryEntityFinder<T extends UnitaryEntity<K>, K extends Serializable>
        extends EntityFinder<T> {
    /**
     * 根据标识获取单一标识实体
     *
     * @param id
     *            标识
     * @return 单一标识实体，如果没有匹配的标识，则返回null
     */
    @Nullable
    T find(K key);
}
