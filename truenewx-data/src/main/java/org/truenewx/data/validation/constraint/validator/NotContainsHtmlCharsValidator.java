package org.truenewx.data.validation.constraint.validator;

import org.truenewx.data.validation.constraint.NotContains;
import org.truenewx.data.validation.constraint.NotContainsHtmlChars;

/**
 * 不能包含HTML字符约束校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NotContainsHtmlCharsValidator
        extends AbstractNotContainsValidator<NotContainsHtmlChars> {

    @Override
    public void initialize(final NotContainsHtmlChars annotation) {
        super.initialize(annotation.annotationType().getAnnotation(NotContains.class));
    }

}
