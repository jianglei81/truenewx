package org.truenewx.data.orm.dao;

import java.io.Serializable;

import org.truenewx.data.model.Entity;
import org.truenewx.data.query.QueryParameter;
import org.truenewx.data.query.QueryResult;

/**
 * 依赖者DAO
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface DependentDao<T extends Entity> extends EntityDao<T> {
    /**
     *
     * @return 依赖类型集合
     */
    Class<?>[] getDependedClasses();

    /**
     * 分页查询从属于指定被依赖实体的当前实体
     *
     * @param dependedClass
     *            被直接或间接依赖实体的类型
     * @param dependedKey
     *            被直接或间接依赖实体的标识
     * @param parameter
     *            查询参数
     * @return 从属于指定被依赖实体的当前实体查询结果
     */
    QueryResult<T> find(Class<?> dependedClass, Serializable dependedKey, QueryParameter parameter);

    /**
     * 判断在指定被依赖实体的数据删除之前，是否需要删除当前实体的数据
     *
     * @param dependedClass
     *            被依赖实体的类型
     * @return 是否需要先删除当前实体的数据
     */
    boolean requiresPreDelete(Class<?> dependedClass);

    /**
     * 根据被依赖实体删除当前实体的数据
     *
     * @param dependedClass
     *            被直接或间接依赖实体的类型
     * @param dependedKey
     *            被直接或间接依赖实体的标识
     * @return 被删除的数量
     */
    int delete(Class<?> dependedClass, Serializable dependedKey);
}
