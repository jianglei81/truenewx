package org.truenewx.core.tuple;

import java.io.Serializable;

import org.truenewx.core.enums.Deviation;

/**
 * 有偏差的数值
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DeviatedNumber<T extends Number> implements Serializable {

    private static final long serialVersionUID = 2187467700636462571L;

    private T value;
    private Deviation deviation;

    public DeviatedNumber(final T value, final Deviation deviation) {
        this.value = value;
        this.deviation = deviation;
    }

    public T getValue() {
        return this.value;
    }

    public Deviation getDeviation() {
        return this.deviation;
    }

}
