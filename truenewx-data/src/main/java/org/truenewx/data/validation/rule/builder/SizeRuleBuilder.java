package org.truenewx.data.validation.rule.builder;

import java.lang.annotation.Annotation;

import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;
import org.truenewx.data.validation.rule.SizeRule;

/**
 * 集合大小规则的构建器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class SizeRuleBuilder implements ValidationRuleBuilder<SizeRule> {

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[] { Size.class };
    }

    @Override
    public void update(final Annotation annotation, final SizeRule rule) {
        if (annotation.annotationType() == Size.class) {
            final Size size = (Size) annotation;
            final int min = size.min();
            if (min > rule.getMin()) {
                rule.setMin(min);
            }
            final int max = size.max();
            if (max < rule.getMax()) {
                rule.setMax(max);
            }
        }
    }

    @Override
    public SizeRule create(final Annotation annotation) {
        if (annotation.annotationType() == Size.class) {
            final Size size = (Size) annotation;
            return new SizeRule(size.min(), size.max());
        }
        return null;
    }

}
