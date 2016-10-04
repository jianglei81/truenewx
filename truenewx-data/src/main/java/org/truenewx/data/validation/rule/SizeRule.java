package org.truenewx.data.validation.rule;

/**
 * 集合大小规则
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class SizeRule extends RangeRule<Integer> {
    public SizeRule(final int min, final int max) {
        super(min, max);
    }

    public SizeRule() {
        this(0, Integer.MAX_VALUE);
    }
}
