package org.truenewx.core.event;

/**
 * 事件总线
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface EventBus extends EventPoster, EventRegistrar {

    /**
     * 默认的事件总线名称
     */
    String DEFAULT_NAME = "default";

}
