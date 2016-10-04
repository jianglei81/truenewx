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
     * 强制将缓存中的数据同步至数据库，但不会提交事务
     */
    void flush();
}
