package org.truenewx.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.truenewx.service.transaction.TransactionalAutoProxyCreator;

/**
 * 服务自动代理创建器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class ServiceAutoProxyCreator extends TransactionalAutoProxyCreator
        implements InitializingBean {

    private ServiceRegistrar serviceRegistrar;

    @Autowired
    public void setServiceRegistrar(final ServiceRegistrar serviceRegistrar) {
        this.serviceRegistrar = serviceRegistrar;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.transactionAttributes == null) { // 如果未配置事务属性则初始化默认配置事务属性
            this.transactionAttributes = new Properties();
            this.transactionAttributes.put("get*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("find*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("load*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("count*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("is*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("validate*", READ_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("add*", WRITE_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("update*", WRITE_TRANSACTION_ATTRIBUTE_ABBR);
            this.transactionAttributes.put("delete*", WRITE_TRANSACTION_ATTRIBUTE_ABBR);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object wrapIfNecessary(Object bean, final String beanName) {
        if (isProxiable(bean, beanName)) {
            // 为服务父类中的全局事务属性配置添加代理
            if (AopUtils.isAopProxy(bean) && bean instanceof Advised) { // 已经是代理，则需取到原始目标对象
                final Advised proxy = (Advised) bean;
                try {
                    bean = proxy.getTargetSource().getTarget();
                } catch (final Exception e) {
                }
            }

            final Object proxy = createProxy(bean, beanName);
            // 注册事务性bean和非事务性bean
            final Class<?>[] proxyInterfaces = getProxyInterfaces(bean.getClass());
            for (final Class<?> proxyInterface : proxyInterfaces) {
                this.serviceRegistrar.register((Class<? extends Service>) proxyInterface,
                        (Service) proxy, (Service) bean);
            }
            return proxy;
        }
        return super.wrapIfNecessary(bean, beanName);
    }

    @Override
    protected boolean isProxiable(final Object bean, final String beanName) {
        // 未被缓存的服务，可取得代理接口类型，即可代理
        if (bean instanceof Service && getCachedProxy(beanName) == null) {
            Class<?> beanClass = bean.getClass();
            if (AopUtils.isAopProxy(bean) && bean instanceof Advised) { // 代理需取目标类型
                final Advised proxy = (Advised) bean;
                beanClass = proxy.getTargetClass();
                if (beanClass == null) {
                    return false;
                }
            }
            final Class<?>[] proxyInterfaces = getProxyInterfaces(beanClass);
            return proxyInterfaces != null && proxyInterfaces.length > 0;
        }
        return super.isProxiable(bean, beanName);
    }

    @Override
    protected Class<?>[] getProxyInterfaces(final Class<?> beanClass) {
        if (Service.class.isAssignableFrom(beanClass)) {
            // 默认取Service实现类所有实现的最底层接口
            final Set<Class<?>> proxyInterfaces = new HashSet<>();
            final Set<Class<?>> beanInterfaces = ClassUtils.getAllInterfacesForClassAsSet(beanClass,
                    this.beanClassLoader);
            for (final Class<?> beanInterface : beanInterfaces) {
                mergeInterface(proxyInterfaces, beanInterface);
            }
            return proxyInterfaces.toArray(new Class<?>[proxyInterfaces.size()]);
        }
        return super.getProxyInterfaces(beanClass);
    }

    private void mergeInterface(final Set<Class<?>> interfaces, final Class<?> interfaceClass) {
        for (final Iterator<Class<?>> iterator = interfaces.iterator(); iterator.hasNext();) {
            final Class<?> next = iterator.next();
            if (interfaceClass.isAssignableFrom(next)) { // 如果是已有接口的父接口，则忽略直接结束
                return;
            }
            if (next.isAssignableFrom(interfaceClass)) { // 如果是已有接口的子接口，则移除该父接口
                iterator.remove();
            }
        }
        interfaces.add(interfaceClass); // 最后加入指定接口
    }
}
