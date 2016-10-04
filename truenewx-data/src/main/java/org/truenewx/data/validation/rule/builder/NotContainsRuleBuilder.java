package org.truenewx.data.validation.rule.builder;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Component;
import org.truenewx.data.validation.constraint.NotContains;
import org.truenewx.data.validation.constraint.NotContainsSpecialChars;
import org.truenewx.data.validation.constraint.NotContainsSqlChars;
import org.truenewx.data.validation.rule.NotContainsRule;

/**
 * 不能包含字符串规则构建器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class NotContainsRuleBuilder implements ValidationRuleBuilder<NotContainsRule> {
    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[] { NotContains.class, NotContainsSqlChars.class,
                NotContainsSpecialChars.class };
    }

    @Override
    public void update(final Annotation annotation, final NotContainsRule rule) {
        final NotContains notContains = getNotContainsAnnotation(annotation);
        if (notContains != null) {
            rule.addValues(notContains.value());
            if (annotation instanceof NotContainsSpecialChars) {
                rule.setNotContainsHtmlChars(true);
            }
        }
    }

    private NotContains getNotContainsAnnotation(final Annotation annotation) {
        if (annotation instanceof NotContains) {
            return (NotContains) annotation;
        } else if (annotation instanceof NotContainsSpecialChars) {
            return annotation.annotationType().getAnnotation(NotContainsSqlChars.class)
                    .annotationType().getAnnotation(NotContains.class);
        } else {
            return annotation.annotationType().getAnnotation(NotContains.class);
        }
    }

    @Override
    public NotContainsRule create(final Annotation annotation) {
        final NotContains notContains = getNotContainsAnnotation(annotation);
        if (notContains != null) {
            final NotContainsRule rule = new NotContainsRule();
            rule.addValues(notContains.value());
            if (annotation instanceof NotContainsSpecialChars) {
                rule.setNotContainsHtmlChars(true);
            }
            return rule;
        }
        return null;
    }

}
