package org.truenewx.web.validation.rule.mapper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.truenewx.core.Strings;
import org.truenewx.data.validation.rule.NotContainsRule;

/**
 * 不能包含字符串规则映射生成器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class NotContainsRuleMapper implements ValidationRuleMapper<NotContainsRule> {

    @Override
    public Map<String, Object> toMap(final NotContainsRule rule, final Locale locale) {
        final Map<String, Object> result = new HashMap<String, Object>();
        if (rule.hasValue()) { // 存在不能包含的字符串
            final String notString = StringUtils.join(rule.getValues(), Strings.SPACE);
            result.put("notContains", HtmlUtils.htmlEscape(notString));
        }
        if (rule.isNotContainsHtmlChars()) {
            result.put("notContainsHtmlChars", Boolean.TRUE);
        }
        return result;
    }
}
