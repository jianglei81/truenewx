package org.truenewx.data.validation.constraint.validator;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.data.validation.constraint.NotContains;

/**
 * 抽象的不能包含约束校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractNotContainsValidator<A extends Annotation>
        implements ConstraintValidator<A, CharSequence> {

    private String[] values;

    protected final void setValues(final String[] values) {
        this.values = values;
    }

    public void initialize(final NotContains annotation) {
        setValues(annotation.value());
    }

    @Override
    public boolean isValid(final CharSequence value, final ConstraintValidatorContext context) {
        if (StringUtils.isNotEmpty(value)) {
            final String s = value.toString();
            for (final String v : this.values) {
                if (s.contains(v)) {
                    return false;
                }
            }
        }
        return true;
    }

}
