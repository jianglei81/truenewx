package org.truenewx.web.validation.generate.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.truenewx.data.validation.rule.DecimalRule;

/**
 * 数值范围的校验映射集生成器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class DecimalRuleMapGenerator implements ValidationMapGenerator<DecimalRule> {

    @Override
    public Map<String, Object> generate(final DecimalRule rule, final Locale locale) {
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("number", Boolean.TRUE); // 至少必须为数值
        final int precision = rule.getPrecision();
        final int scale = rule.getScale();
        if (scale >= 0 && precision > scale) { // 精度大于等于0且长度大于精度才有效，不支持负精度
            if (scale == 0) { // 小数位精度为0，则限定为整数
                result.put("int", Boolean.TRUE);
                result.put("maxLength", precision);
                result.remove("number");
            } else {
                result.put("integer", precision - scale); // 整数部分长度
                result.put("scale", scale);
            }
        }
        final BigDecimal min = rule.getMin();
        if (min != null && min.compareTo(DecimalRule.MIN_DECIMAL) > 0) {
            result.put("minValue", min);
        }
        final BigDecimal max = rule.getMax();
        if (max != null && max.compareTo(DecimalRule.MAX_DECIMAL) < 0) {
            result.put("maxValue", max);
        }
        return result;
    }

}
