package org.truenewx.web.validation.generate;

import java.util.Locale;
import java.util.Set;

import org.truenewx.data.validation.rule.ValidationRule;

/**
 * 校验生成器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public interface ValidationGenerator {
    /**
     * 用指定校验规则集生成在页面中的表达式
     * 
     * @param rules
     *            校验规则
     * @param locale
     *            区域
     * @return 在页面中的表达式
     */
    String generateExpression(Set<ValidationRule> rules, Locale locale);
}
