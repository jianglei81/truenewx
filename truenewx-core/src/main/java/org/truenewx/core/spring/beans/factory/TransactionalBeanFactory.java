package org.truenewx.core.spring.beans.factory;

import org.springframework.beans.factory.BeanFactory;

/**
 * 事务性Bean工厂
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface TransactionalBeanFactory extends BeanFactory {

    <T> T getBean(String name, boolean transactional);

    <T> T getBean(String name, Class<T> requiredType, boolean transactional);

    <T> T getBean(Class<T> requiredType, boolean transactional);

    boolean containsBean(String name, boolean transactional);

}
