package org.truenewx.data.orm.dao;

/**
 * 数据访问对象
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            数据实体类型
 */
public interface Dao<T> extends EntityDao<T> {

    /**
     * 获取已有数据中的第一条，常用于单元测试，谨慎用于它处
     *
     * @return 第一条数据记录
     */
    T first();

    /**
     * 强制将缓存中的数据同步至数据库，但不会提交事务
     */
    void flush();
}
