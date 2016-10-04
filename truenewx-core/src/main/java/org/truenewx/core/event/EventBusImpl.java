package org.truenewx.core.event;

import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.Strings;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.functor.algorithm.impl.AlgoFirst;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.core.spring.transaction.annotation.WriteTransactional;
import org.truenewx.core.util.BeanUtil;

import com.google.common.eventbus.SubscriberBasedDispatcher;

/**
 * 事件总线实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EventBusImpl extends com.google.common.eventbus.EventBus
                implements EventBus, ContextInitializedBean {

    private SubscriberExceptionCollector exceptionCollector;

    public EventBusImpl() throws Exception {
        this(DEFAULT_NAME, null);
    }

    public EventBusImpl(final String name) throws Exception {
        this(name, null);
    }

    public EventBusImpl(final Executor executor) throws Exception {
        this(DEFAULT_NAME, executor);
    }

    public EventBusImpl(final String name, final Executor executor) throws Exception {
        super(name);
        this.exceptionCollector = new SubscriberExceptionCollector();
        BeanUtil.setFieldValue(this, "exceptionHandler", this.exceptionCollector);
        BeanUtil.setFieldValue(this, "dispatcher", new SubscriberBasedDispatcher(executor));
    }

    @Override
    @WriteTransactional
    public void post(final Event event) throws HandleableException {
        super.post(event);
        final SubscriberExceptionCollection exceptionCollection = this.exceptionCollector
                        .pull(event);
        if (exceptionCollection != null) {
            // 如果有底层异常，先抛出底层异常，因为底层异常一般是由代码缺陷导致的，应优先解决
            final Throwable t = AlgoFirst.visit(exceptionCollection.getThrowables(), null);
            if (t != null) {
                t.printStackTrace();
                throw new RuntimeException(t);
            }
            // 如果没有底层异常但有可处理异常，则抛出可处理异常
            final HandleableException he = exceptionCollection.getHandleableException();
            if (he != null) {
                throw he;
            }
        }
    }

    @Override
    public void register(Object object) {
        object = getActualObject(object);
        super.register(object);
    }

    private Object getActualObject(Object object) {
        if (AopUtils.isAopProxy(object)) {
            try {
                object = ((Advised) object).getTargetSource().getTarget();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    @Override
    public void unregister(Object object) {
        object = getActualObject(object);
        super.unregister(object);
    }

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final Map<String, EventSubscriber> beans = context.getBeansOfType(EventSubscriber.class);
        for (final EventSubscriber subscriber : beans.values()) {
            final String name = subscriber.getEventBusName();
            if (StringUtils.isBlank(name)) { // 提供的名称为空时，注册到默认总线
                if (DEFAULT_NAME.equals(identifier())) {
                    register(subscriber);
                }
            } else {
                final String[] names = name.split(Strings.COMMA);
                if (ArrayUtils.contains(names, identifier())) {
                    register(subscriber);
                }
            }
        }
    }

}
