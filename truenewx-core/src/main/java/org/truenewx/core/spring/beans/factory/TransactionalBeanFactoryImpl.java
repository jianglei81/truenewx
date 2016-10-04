package org.truenewx.core.spring.beans.factory;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

/**
 * 事务性Bean提交处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class TransactionalBeanFactoryImpl implements TransactionalBeanFactory, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(final String name, final boolean transactional) {
        try {
            final T bean = (T) this.beanFactory.getBean(name);
            return getTarget(bean, transactional);
        } catch (final Exception e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T getTarget(final T bean, final boolean transactional) throws Exception {
        if (AopUtils.isAopProxy(bean)) {
            if (transactional) { // 取到的Bean为代理且需要的就是事务性Bean，则返回该Bean
                return bean;
            } else if (bean instanceof Advised) { // 取到的Bean为代理但需要非事务性Bean，则返回该代理的代理目标
                final Advised proxy = (Advised) bean;
                return (T) proxy.getTargetSource().getTarget();
            }
        } else if (!transactional) { // 取到的Bean非代理且需要的就是非事务性Bean，则返回该Bean
            return bean;
        }
        return null;
    }

    @Override
    public <T> T getBean(final String name, final Class<T> requiredType, final boolean transactional) {
        try {
            final T bean = this.beanFactory.getBean(name, requiredType);
            return getTarget(bean, transactional);
        } catch (final Exception e) {
        }
        return null;
    }

    @Override
    public <T> T getBean(final Class<T> requiredType, final boolean transactional) {
        try {
            final T bean = this.beanFactory.getBean(requiredType);
            return getTarget(bean, transactional);
        } catch (final Exception e) {
        }
        return null;
    }

    @Override
    public boolean containsBean(final String name, final boolean transactional) {
        return getBean(name, transactional) != null;
    }

    @Override
    public Object getBean(final String name) throws BeansException {
        return this.beanFactory.getBean(name);
    }

    @Override
    public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
        return this.beanFactory.getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(final Class<T> requiredType) throws BeansException {
        return this.beanFactory.getBean(requiredType);
    }

    @Override
    public Object getBean(final String name, final Object... args) throws BeansException {
        return this.beanFactory.getBean(name, args);
    }

    @Override
    public boolean containsBean(final String name) {
        return this.beanFactory.containsBean(name);
    }

    @Override
    public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
        return this.beanFactory.isSingleton(name);
    }

    @Override
    public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
        return this.beanFactory.isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(final String name, final Class<?> targetType)
            throws NoSuchBeanDefinitionException {
        return this.beanFactory.isTypeMatch(name, targetType);
    }

    @Override
    public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getType(name);
    }

    @Override
    public String[] getAliases(final String name) {
        return this.beanFactory.getAliases(name);
    }

}
