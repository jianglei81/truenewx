package org.truenewx.data.validation.rule;

/**
 * 校验规则
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class ValidationRule {

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        // 一组校验规则中，同一种规则最多只能有一个
        return obj != null && getClass() == obj.getClass();
    }

}
