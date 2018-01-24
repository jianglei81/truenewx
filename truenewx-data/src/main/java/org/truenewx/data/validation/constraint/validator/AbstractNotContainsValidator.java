package org.truenewx.data.validation.constraint.validator;

import java.lang.annotation.Annotation;
import java.util.Collection;

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
        implements ConstraintValidator<A, Object> {

    private String[] values;

    protected final void setValues(final String[] values) {
        this.values = values;
    }

    public void initialize(final NotContains annotation) {
        setValues(annotation.value());
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (value instanceof CharSequence) {
            final String s = value.toString();
            if (StringUtils.isNotEmpty(s)) {
                for (final String v : this.values) {
                    if (s.contains(v)) {
                        return false;
                    }
                }
            }
        } else if (value instanceof Collection) {
            final Collection<?> collection = (Collection<?>) value;
            for (final Object obj : collection) {
                if (!isValid(obj, context)) {
                    return false;
                }
            }
        } else if (value instanceof Object[]) {
            final Object[] array = (Object[]) value;
            for (final Object obj : array) {
                if (!isValid(obj, context)) {
                    return false;
                }
            }
        }
        return true;
    }

}
