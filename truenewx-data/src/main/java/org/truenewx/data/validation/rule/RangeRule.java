package org.truenewx.data.validation.rule;

/**
 * 范围规则
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            值类型
 */
public abstract class RangeRule<T> extends ValidationRule {

    private T min;
    private T max;

    public RangeRule(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return this.min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return this.max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    @Override
    public boolean isEmpty() {
        return this.min == null && this.max == null;
    }
}
