package org.truenewx.data.orm.dao.support;

import org.truenewx.data.orm.dao.EntityDao;

/**
 * DAO工厂
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface DaoFactory {

    <D extends EntityDao<?>> D getDaoByDaoClass(Class<D> daoClass);

    <T, D extends EntityDao<T>> D getDaoByEntityClass(Class<T> entityClass);

}
