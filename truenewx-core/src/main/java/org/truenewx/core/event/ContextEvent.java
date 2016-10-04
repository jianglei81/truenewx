package org.truenewx.core.event;

/**
 * 具有上下文的事件
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface ContextEvent<C> extends Event {

    public <T extends C> T getContext();

}
