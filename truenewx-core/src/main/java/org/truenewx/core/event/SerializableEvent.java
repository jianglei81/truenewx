package org.truenewx.core.event;

import java.io.Serializable;

import org.truenewx.core.model.Named;

/**
 * 可序列化的事件
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SerializableEvent implements ContextEvent<Serializable>, Named, Serializable {

    private static final long serialVersionUID = 6973710311663090510L;

    private String name;
    /**
     * 上下文
     */
    private Serializable context;

    public SerializableEvent(final String name, final Serializable context) {
        this.name = name;
        this.context = context;
    }

    public SerializableEvent(final String name) {
        this(name, null);
    }

    public SerializableEvent() {
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getContext() {
        return (T) this.context;
    }

    public void setContext(final Serializable context) {
        this.context = context;
    }

}
