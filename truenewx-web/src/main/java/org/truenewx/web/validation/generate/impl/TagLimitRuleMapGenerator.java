package org.truenewx.web.validation.generate.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.truenewx.core.Strings;
import org.truenewx.data.validation.rule.TagLimitRule;

/**
 * 标签限定规则映射生成器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class TagLimitRuleMapGenerator implements ValidationMapGenerator<TagLimitRule> {

    @Override
    public Map<String, Object> generate(final TagLimitRule rule, final Locale locale) {
        final Map<String, Object> result = new HashMap<String, Object>();
        final Set<String> allowed = rule.getAllowed();
        final Set<String> forbidden = rule.getForbidden();
        if (allowed.isEmpty() && forbidden.isEmpty()) { // 不允许所有标签
            result.put("rejectTags", Boolean.TRUE);
        } else {
            if (allowed.size() > 0) { // 存在仅允许的标签
                result.put("allowedTags", StringUtils.join(allowed, Strings.COMMA));
            }
            if (forbidden.size() > 0) { // 存在禁止的标签
                result.put("forbiddenTags", StringUtils.join(forbidden, Strings.COMMA));
            }
        }
        return result;
    }

}
