package org.truenewx.data.validation.rule.builder;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Component;
import org.truenewx.data.validation.constraint.NotContains;
import org.truenewx.data.validation.constraint.NotContainsAngleBracket;
import org.truenewx.data.validation.constraint.NotContainsHtmlChars;
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
                NotContainsAngleBracket.class, NotContainsHtmlChars.class,
                NotContainsSpecialChars.class };
    }

    @Override
    public void update(Annotation annotation, NotContainsRule rule) {
        NotContains notContains = getNotContainsAnnotation(annotation);
        if (notContains != null) {
            rule.addValues(notContains.value());
            if (annotation instanceof NotContainsAngleBracket) {
                rule.setAngleBracket(true);
            } else if (annotation instanceof NotContainsHtmlChars) {
                rule.setHtml(true);
            } else if (annotation instanceof NotContainsSpecialChars) {
                rule.setHtml(((NotContainsSpecialChars) annotation).html());
            }
        }
    }

    private NotContains getNotContainsAnnotation(Annotation annotation) {
        if (annotation instanceof NotContains) {
            return (NotContains) annotation;
        } else if (annotation instanceof NotContainsSpecialChars) {
            if (((NotContainsSpecialChars) annotation).sql()) {
                return NotContainsSqlChars.class.getAnnotation(NotContains.class);
            }
            return null;
        } else {
            return annotation.annotationType().getAnnotation(NotContains.class);
        }
    }

    @Override
    public NotContainsRule create(Annotation annotation) {
        NotContains notContains = getNotContainsAnnotation(annotation);
        if (notContains != null) {
            NotContainsRule rule = new NotContainsRule();
            rule.addValues(notContains.value());
            if (annotation instanceof NotContainsAngleBracket) {
                rule.setAngleBracket(true);
            } else if (annotation instanceof NotContainsHtmlChars) {
                rule.setHtml(true);
            } else if (annotation instanceof NotContainsSpecialChars) {
                rule.setHtml(((NotContainsSpecialChars) annotation).html());
            }
            return rule;
        }
        return null;
    }

}
