package org.truenewx.web.spring.servlet;

import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.truenewx.core.spring.util.PlaceholderResolver;

/**
 * ServletContext属性初始化加载工厂
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class ServletContextInitFactory
                implements InitializingBean, ServletContextAware, ApplicationContextAware {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private ServletContext servletContext;
    private ApplicationContext applicationContext;
    private PlaceholderResolver placeholderResolver;

    @Override
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext)
                    throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired(required = false)
    public void setPlaceholderResolver(final PlaceholderResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.servletContext != null) {
            // 加载初始化bean
            if (this.applicationContext != null) {
                final Map<String, ServletContextInitBean> initBeans = this.applicationContext
                                .getBeansOfType(ServletContextInitBean.class);
                for (final Map.Entry<String, ServletContextInitBean> entry : initBeans.entrySet()) {
                    final ServletContextInitBean initBean = entry.getValue();
                    final String attrName = initBean.getAttributeName();
                    if (attrName != null) {
                        this.servletContext.setAttribute(attrName, initBean);
                        this.logger.info("Loaded bean: {} to ServletContext.", attrName);
                    }
                }
            }
            // 加载资源文件常量
            if (this.placeholderResolver != null) {
                for (final String placeholderKey : this.placeholderResolver.getPlaceholderKeys()) {
                    final String value = this.placeholderResolver
                                    .resolvePlaceholder(placeholderKey);
                    this.servletContext.setAttribute(placeholderKey, value);
                    this.logger.info("Loaded placeholder: {} to ServletContext.", placeholderKey);
                }
            } else {
                this.logger.warn("The placeholderResolver is null.");
            }
        }
    }

}
