package org.truenewx.data.model.unity;

import java.io.Serializable;

import org.truenewx.core.event.Event;

/**
 * 单体事件
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UnityEvent<K extends Serializable> implements Event {

    /**
     * 单体标识
     */
    private K id;

    public UnityEvent(final K id) {
        this.id = id;
    }

    /**
     *
     * @return 单体标识
     */
    public K getId() {
        return this.id;
    }

}
