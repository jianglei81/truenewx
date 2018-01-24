package org.truenewx.data.validation.constraint.validator;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.truenewx.core.Strings;
import org.truenewx.core.util.CollectionUtil;
import org.truenewx.data.validation.constraint.NotContains;
import org.truenewx.data.validation.constraint.NotContainsHtmlChars;
import org.truenewx.data.validation.constraint.NotContainsSpecialChars;
import org.truenewx.data.validation.constraint.NotContainsSqlChars;

/**
 * 不能包含特殊字符约束校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NotContainsSpecialCharsValidator
    extends AbstractNotContainsValidator<NotContainsSpecialChars> {

    @Override
    public void initialize(final NotContainsSpecialChars annotation) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        final Set<String> values = new HashSet<>();
        CollectionUtil.addAll(values, annotationType.getAnnotation(NotContainsHtmlChars.class)
            .annotationType().getAnnotation(NotContains.class).value());
        CollectionUtil.addAll(values, annotationType.getAnnotation(NotContainsSqlChars.class)
            .annotationType().getAnnotation(NotContains.class).value());
        if (annotation.comma()) {
            values.add(Strings.COMMA);
        }
        setValues(values.toArray(new String[values.size()]));
    }

}
