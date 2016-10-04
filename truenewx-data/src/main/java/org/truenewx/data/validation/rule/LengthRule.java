package org.truenewx.data.validation.rule;

/**
 * 字符串长度规则
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class LengthRule extends RangeRule<Integer> {

    public LengthRule(final int min, final int max) {
        super(min, max);
    }

    public LengthRule() {
        this(0, Integer.MAX_VALUE);
    }
}
