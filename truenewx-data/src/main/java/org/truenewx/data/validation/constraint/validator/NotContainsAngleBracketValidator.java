package org.truenewx.data.validation.constraint.validator;

import org.truenewx.data.validation.constraint.NotContains;
import org.truenewx.data.validation.constraint.NotContainsAngleBracket;

/**
 * 不能包含尖括弧约束校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NotContainsAngleBracketValidator
        extends AbstractNotContainsValidator<NotContainsAngleBracket> {

    @Override
    public void initialize(final NotContainsAngleBracket annotation) {
        super.initialize(annotation.annotationType().getAnnotation(NotContains.class));
    }

}
