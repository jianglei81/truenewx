package org.truenewx.data.validation.rule.builder;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.stereotype.Component;
import org.truenewx.data.validation.constraint.NotContainsAngleBracket;
import org.truenewx.data.validation.constraint.NotContainsHtmlChars;
import org.truenewx.data.validation.rule.MarkRule;

/**
 * 标识规则的构建器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class MarkRuleBuilder implements ValidationRuleBuilder<MarkRule> {
    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[] { NotNull.class, NotEmpty.class, NotBlank.class, Email.class,
                URL.class, NotContainsAngleBracket.class, NotContainsHtmlChars.class };
    }

    @Override
    public void update(final Annotation annotation, final MarkRule rule) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        final Collection<Class<? extends Annotation>> annotationTypes = rule.getAnnotationTypes();
        if (annotationType == NotBlank.class) {
            // 覆盖不能为空和不能为null
            annotationTypes.remove(NotEmpty.class);
            annotationTypes.remove(NotNull.class);
        } else if (annotationType == NotEmpty.class) {
            // 已经有不能为空白，则忽略
            if (annotationTypes.contains(NotBlank.class)) {
                return;
            }
            // 覆盖不能为null
            annotationTypes.remove(NotNull.class);
        } else if (annotationType == NotNull.class) {
            // 已经有不能为空白或不能为空，则忽略
            if (annotationTypes.contains(NotBlank.class)
                    || annotationTypes.contains(NotEmpty.class)) {
                return;
            }
        }
        annotationTypes.add(annotationType);
    }

    @Override
    public MarkRule create(final Annotation annotation) {
        return new MarkRule(annotation.annotationType());
    }

}
