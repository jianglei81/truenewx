package org.truenewx.core.event;

import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * 订阅者异常采集器，作为订阅者异常处理器，仅进行收集不进行其它处理
 *
 * @author jianglei
 * @since JDK 1.8
 */
class SubscriberExceptionCollector implements SubscriberExceptionHandler {

    private Map<Object, SubscriberExceptionCollection> exceptionCollections = new HashMap<>();

    @Override
    public void handleException(final Throwable exception,
            final SubscriberExceptionContext context) {
        final Object event = context.getEvent();
        SubscriberExceptionCollection sec = this.exceptionCollections.get(event);
        if (sec == null) {
            sec = new SubscriberExceptionCollection();
            this.exceptionCollections.put(event, sec);
        }
        sec.add(exception);
    }

    /**
     * 拉出指定事件被订阅者处理后可能出现的异常集合，拉出后该集合不再保持
     *
     * @param event
     *            事件
     * @return 可能的异常集合
     */
    public SubscriberExceptionCollection pull(final Event event) {
        return this.exceptionCollections.remove(event);
    }

}
