package org.truenewx.data.orm.dao.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.data.orm.dao.EntityDao;

/**
 * DAO工厂实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Repository
public class DaoFactoryImpl implements DaoFactory, ApplicationContextAware, ContextInitializedBean {

    private ApplicationContext context;
    private Map<Class<?>, EntityDao<?>> daos = new HashMap<>();

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        this.context = context;
        @SuppressWarnings("rawtypes")
        final Map<String, EntityDao> beans = this.context.getBeansOfType(EntityDao.class);
        for (final EntityDao<?> dao : beans.values()) {
            this.daos.put(dao.getEntityClass(), dao);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <D extends EntityDao<?>> D getDaoByDaoClass(final Class<D> daoClass) {
        D dao = (D) this.daos.get(daoClass);
        if (dao == null) {
            try {
                dao = this.context.getBean(daoClass);
                this.daos.put(daoClass, dao);
            } catch (final BeansException e) {
            }
        }
        return dao;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, D extends EntityDao<T>> D getDaoByEntityClass(final Class<T> entityClass) {
        return (D) this.daos.get(entityClass);
    }

}
