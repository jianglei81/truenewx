package org.truenewx.core.event;


/**
 * 事件订阅者
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface EventSubscriber {
    /**
     * 获取要注册的事件总线名称，多个名称用英文逗号分隔
     *
     * @return 要注册的事件总线名称
     */
    String getEventBusName();
}
