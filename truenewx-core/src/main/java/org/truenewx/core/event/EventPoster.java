package org.truenewx.core.event;

import org.truenewx.core.exception.HandleableException;

/**
 * 事件发布器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface EventPoster {
    /**
     * 发布事件，同步响应，等待所有事件响应处理完毕后才返回
     *
     * @param event
     *            事件
     * @throws 如果事件响应处理过程中出现可处理的异常
     */
    void post(Event event) throws HandleableException;

}
