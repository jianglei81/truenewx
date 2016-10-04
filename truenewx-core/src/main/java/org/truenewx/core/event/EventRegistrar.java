package org.truenewx.core.event;


/**
 * 事件注册器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface EventRegistrar {

    /**
     * 注册事件处理对象
     *
     * @param object
     *            事件处理对象
     */
    void register(Object object);

    /**
     * 注销事件处理对象
     *
     * @param object
     *            事件处理对象
     */
    void unregister(Object object);
}
