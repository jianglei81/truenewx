package org.truenewx.data.orm.dao;

import org.truenewx.data.finder.EntityFinder;

/**
 * 实体DAO
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            实体类型，如果限定继承Entity，会导致maven编译失败，而且Entity为更上层的接口，不应引用
 */
public interface EntityDao<T> extends EntityFinder<T> {

    /**
     *
     * @return 实体类型
     */
    Class<T> getEntityClass();

    /**
     * 保存指定实体对象
     *
     * @param entity
     *            实体对象
     */
    void save(T entity);

    /**
     * 删除指定实体对象
     *
     * @param entity
     *            实体对象
     */
    void delete(T entity);

    /**
     * 刷新指定实体对象，从数据库读取最新数据
     *
     * @param entity
     *            实体对象
     */
    void refresh(T entity);

    /**
     * 计算所有实体的总数
     *
     * @return 所有实体的总数
     */
    int countAll();
}
