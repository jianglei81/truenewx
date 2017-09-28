package org.truenewx.data.orm.hibernate;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;

/**
 * 标准服务注册器的适配实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class StandardServiceRegistryAdapter implements StandardServiceRegistry {

    private ServiceRegistry target;

    public StandardServiceRegistryAdapter(final ServiceRegistry serviceRegistry) {
        this.target = serviceRegistry;
    }

    @Override
    public ServiceRegistry getParentServiceRegistry() {
        return this.target.getParentServiceRegistry();
    }

    @Override
    public <R extends Service> R getService(final Class<R> serviceRole) {
        return this.target.getService(serviceRole);
    }

    @Override
    public <R extends Service> R requireService(final Class<R> serviceRole) {
        return this.target.requireService(serviceRole);
    }

}
