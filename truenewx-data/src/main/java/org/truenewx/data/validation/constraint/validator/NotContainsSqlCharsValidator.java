package org.truenewx.data.validation.constraint.validator;

import org.truenewx.data.validation.constraint.NotContains;
import org.truenewx.data.validation.constraint.NotContainsSqlChars;

/**
 * 不能包含SQL字符约束校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NotContainsSqlCharsValidator
        extends AbstractNotContainsValidator<NotContainsSqlChars> {

    @Override
    public void initialize(final NotContainsSqlChars annotation) {
        super.initialize(annotation.annotationType().getAnnotation(NotContains.class));
    }

}
