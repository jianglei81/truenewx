package org.truenewx.service.relation;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.relation.Relation;

/**
 * 简单关系服务
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
public interface SimpleRelationService<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends RelationService<T, L, R> {

    /**
     * 添加关系
     * 
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @param relation
     *            存放添加数据的关系对象
     *
     * @return 添加成功的关系
     * @throws HandleableException
     *             如果添加校验失败
     */
    T add(L leftId, R rightId, T relation) throws HandleableException;

    /**
     * 修改关系
     *
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @param relation
     *            存放修改数据的关系对象
     * @return 修改后的关系
     * @throws HandleableException
     *             如果修改校验失败
     */
    T update(L leftId, R rightId, T relation) throws HandleableException;
}
