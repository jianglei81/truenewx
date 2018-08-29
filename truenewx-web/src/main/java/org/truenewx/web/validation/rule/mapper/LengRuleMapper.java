package org.truenewx.web.validation.rule.mapper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.truenewx.data.validation.rule.LengthRule;

/**
 * 字符串长度范围的校验映射集生成器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class LengRuleMapper implements ValidationRuleMapper<LengthRule> {

    @Override
    public Map<String, Object> toMap(final LengthRule rule, final Locale locale) {
        final Map<String, Object> result = new HashMap<String, Object>();
        final Integer min = rule.getMin();
        if (min != null && min > 0) {
            result.put("minLength", min);
        }
        final Integer max = rule.getMax();
        if (max != null && max < Integer.MAX_VALUE) {
            result.put("maxLength", max);
        }
        return result;
    }

}
