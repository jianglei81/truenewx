package org.truenewx.web.validation.generate.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.truenewx.core.Strings;
import org.truenewx.data.validation.rule.RegexRule;

/**
 * 正则表达式规则的校验映射集生成器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class RegexRuleMapGenerator
                implements ValidationMapGenerator<RegexRule>, MessageSourceAware {
    private MessageSource messageSource;

    @Override
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Map<String, Object> generate(final RegexRule rule, final Locale locale) {
        String message = rule.getMessage();
        if (StringUtils.isNotBlank(message) && message.startsWith("{") && message.endsWith("}")) {
            final String code = message.substring(1, message.length() - 1);
            message = this.messageSource.getMessage(code, null, Strings.EMPTY, locale);
        }
        if (message == null) {
            message = Strings.EMPTY;
        } else {
            message = HtmlUtils.htmlEscape(message);
        }
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("regex", new String[] { rule.getExpression(), message });
        return result;
    }

}
