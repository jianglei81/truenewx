package org.truenewx.data.validation.rule.builder;

import java.lang.annotation.Annotation;

import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.truenewx.data.validation.rule.RegexRule;

/**
 * 正则表达式规则构建器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class RegexRuleBuilder implements ValidationRuleBuilder<RegexRule> {
    /**
     * 默认消息
     */
    public static final String DEFAULT_MESSAGE = getDefaultMessage(Pattern.class);

    /**
     * 获取指定注解类型对应的默认消息
     * 
     * @param annoClass
     *            注解类型
     * @return 对应的默认消息
     */
    protected static String getDefaultMessage(final Class<?> annoClass) {
        return StringUtils.join("{", annoClass.getName(), ".message}");
    }

    @Override
    public Class<?>[] getConstraintTypes() {
        return new Class<?>[] { Pattern.class };
    }

    @Override
    public void update(final Annotation annotation, final RegexRule rule) {
        if (annotation.annotationType() == Pattern.class) {
            final Pattern pattern = (Pattern) annotation;
            final String expression = pattern.regexp();
            if (StringUtils.isNotBlank(expression)) {
                rule.setExpression(expression);
            }
            final String message = pattern.message();
            if (!DEFAULT_MESSAGE.equals(message)) {
                rule.setMessage(message);
            }
        }
    }

    @Override
    public RegexRule create(final Annotation annotation) {
        if (annotation.annotationType() == Pattern.class) {
            final Pattern pattern = (Pattern) annotation;
            return new RegexRule(pattern.regexp(), pattern.message());
        }
        return null;
    }

}
