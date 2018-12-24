package org.truenewx.data.validation.rule.builder;

import java.lang.annotation.Annotation;

import org.springframework.stereotype.Component;
import org.truenewx.core.Strings;
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
        }
        if (annotation instanceof NotContainsAngleBracket) {
            rule.setNotContainsAngleBracket(true);
        } else if (annotation instanceof NotContainsHtmlChars) {
            rule.setNotContainsHtmlChars(true);
        } else if (annotation instanceof NotContainsSpecialChars) {
            NotContainsSpecialChars notContainsSpecialChars = (NotContainsSpecialChars) annotation;
            if (!notContainsSpecialChars.comma()) {
                rule.addValues(Strings.COMMA);
            }
            rule.setNotContainsHtmlChars(!notContainsSpecialChars.html());
        }
    }

    private NotContains getNotContainsAnnotation(Annotation annotation) {
        if (annotation instanceof NotContains) {
            return (NotContains) annotation;
        } else if (annotation instanceof NotContainsSpecialChars) {
            if (!((NotContainsSpecialChars) annotation).sql()) {
                return NotContainsSqlChars.class.getAnnotation(NotContains.class);
            }
            return null;
        } else {
            return annotation.annotationType().getAnnotation(NotContains.class);
        }
    }

    @Override
    public NotContainsRule create(Annotation annotation) {
        NotContainsRule rule = new NotContainsRule();
        update(annotation, rule);
        return rule;
    }

}
