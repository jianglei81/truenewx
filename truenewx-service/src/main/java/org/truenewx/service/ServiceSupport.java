package org.truenewx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.spring.beans.factory.TransactionalBeanFactory;
import org.truenewx.data.orm.dao.EntityDao;
import org.truenewx.data.orm.dao.support.DaoFactory;

/**
 * 服务支持，具有快速获取其它服务和DAO的能力
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class ServiceSupport {

    private TransactionalBeanFactory beanFactory;
    private DaoFactory daoFactory;
    private ServiceFactory serviceFactory;

    public void setBeanFactory(final TransactionalBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Autowired
    public void setDaoFactory(final DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Autowired
    public void setServiceFactory(final ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    /**
     * 获取指定类型的bean。无缓存，请谨慎使用
     *
     * @param beanClass
     *            类型
     * @return bean
     */
    protected final <B> B getBean(final Class<B> beanClass) {
        B bean = this.beanFactory.getBean(beanClass, false);
        if (bean == null) {
            bean = this.beanFactory.getBean(beanClass, true);
        }
        return bean;
    }

    protected final <D extends EntityDao<?>> D getDao(final Class<D> daoClass) {
        return this.daoFactory.getDaoByDaoClass(daoClass);
    }

    protected final <S extends Service> S getTransactionalService(final Class<S> serviceClass) {
        return this.serviceFactory.getService(serviceClass, true);
    }

    protected final <S extends Service> S getService(final Class<S> serviceClass) {
        S service = this.serviceFactory.getService(serviceClass, false); // 默认取非事务性服务
        if (service == null) { // 如果没有非事务性服务，则取事务性服务
            service = getTransactionalService(serviceClass);
        }
        return service;
    }

}
