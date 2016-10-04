package com.google.common.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import org.truenewx.core.annotation.Asynchronous;
import org.truenewx.core.util.BeanUtil;
import org.truenewx.core.util.concurrent.DefaultThreadPoolExecutor;

/**
 * 基于订阅者的情况进行调度的调度器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SubscriberBasedDispatcher extends Dispatcher {

    private Executor executor;

    public SubscriberBasedDispatcher(final Executor executor) {
        this.executor = executor == null ? new DefaultThreadPoolExecutor(4) : executor;
    }

    @Override
    void dispatch(final Object event, final Iterator<Subscriber> subscribers) {
        final List<Subscriber> asynSubscribers = new ArrayList<>(); // 异步响应的订阅者清单
        final List<Subscriber> syncSubscribers = new ArrayList<>(); // 同步响应的订阅者清单
        while (subscribers.hasNext()) {
            final Subscriber subscriber = subscribers.next();
            if (isAsyn(subscriber)) { // 异步响应
                // 设置执行器为多线程池执行器，以达到异步响应的目的
                BeanUtil.setFieldValue(subscriber, "executor", this.executor);
                asynSubscribers.add(subscriber);
            } else { // 同步响应
                syncSubscribers.add(subscriber);
            }
        }
        // 先调度所有异步响应，以免阻塞后续调度
        for (final Subscriber subscriber : asynSubscribers) {
            subscriber.dispatchEvent(event);
        }
        // 后调度所有同步响应
        for (final Subscriber subscriber : syncSubscribers) {
            subscriber.dispatchEvent(event);
        }
    }

    private boolean isAsyn(final Subscriber subscriber) {
        final Method method = BeanUtil.getFieldValue(subscriber, "method");
        return method != null && method.getAnnotation(Asynchronous.class) != null;
    }

}
