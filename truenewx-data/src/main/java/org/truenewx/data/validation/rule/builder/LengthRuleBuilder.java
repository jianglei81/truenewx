package org.truenewx.data.validation.rule.builder;

import java.lang.annotation.Annotation;

import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;
import org.truenewx.data.validation.rule.LengthRule;

/**
 * 字符串长度规则的构建器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class LengthRuleBuilder implements ValidationRuleBuilder<LengthRule> {

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[] { Length.class };
    }

    @Override
    public void update(final Annotation annotation, final LengthRule rule) {
        if (annotation.annotationType() == Length.class) {
            final Length length = (Length) annotation;
            final int min = length.min();
            if (min > rule.getMin()) {
                rule.setMin(min);
            }
            final int max = length.max();
            if (max < rule.getMax()) {
                rule.setMax(max);
            }
        }
    }

    @Override
    public LengthRule create(final Annotation annotation) {
        if (annotation.annotationType() == Length.class) {
            final Length length = (Length) annotation;
            return new LengthRule(length.min(), length.max());
        }
        return null;
    }

}
