package org.truenewx.core.spring.beans.factory.config;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 默认区域配置处理器。为了让设置默认区域的动作在所有Bean初始化之前执行
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DefaultLocaleBeanPostProcessor implements BeanPostProcessor {

    public DefaultLocaleBeanPostProcessor() {
        setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
    }

    public void setDefaultLocale(final Locale locale) {
        Locale.setDefault(locale);
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName)
            throws BeansException {
        return bean;
    }

}
