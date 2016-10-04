package org.truenewx.core.spring.context;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.core.spring.beans.ContextInitializedBeanProxy;

/**
 * 容器初始化完成后执行bean的监听器，找出所有容器初始化完成后执行bean并在容器初始化完成后执行。<br/>
 * 如果一个bean具有代理，则只执行代理
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class ContextInitializedBeanListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        final ApplicationContext context = event.getApplicationContext();
        final Map<String, ContextInitializedBean> beans = context
                        .getBeansOfType(ContextInitializedBean.class);
        final Map<ContextInitializedBean, ContextInitializedBean> map = new HashMap<ContextInitializedBean, ContextInitializedBean>();
        for (final ContextInitializedBean bean : beans.values()) {
            if (bean instanceof ContextInitializedBeanProxy) {
                final ContextInitializedBeanProxy proxy = (ContextInitializedBeanProxy) bean;
                final ContextInitializedBean target = proxy.getTarget();
                if (target != null) {
                    map.remove(target);
                    map.put(target, proxy);
                }
            } else {
                if (map.get(bean) == null) {
                    map.put(bean, bean);
                }
            }
        }
        for (final ContextInitializedBean bean : map.values()) {
            try {
                bean.afterInitialized(context);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
