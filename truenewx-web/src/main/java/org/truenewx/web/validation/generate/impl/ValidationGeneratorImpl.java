package org.truenewx.web.validation.generate.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.data.validation.rule.ValidationRule;
import org.truenewx.web.validation.generate.ValidationGenerator;

/**
 * 校验生成器默认实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class ValidationGeneratorImpl implements ValidationGenerator, ContextInitializedBean {

    private Map<Class<?>, ValidationMapGenerator<ValidationRule>> ruleGenerators = new HashMap<>();

    @Override
    public String generateExpression(final Set<ValidationRule> rules, final Locale locale) {
        final Map<String, Object> map = new LinkedHashMap<>(); // 保留顺序
        for (final ValidationRule rule : rules) {
            final ValidationMapGenerator<ValidationRule> ruleGenerator = this.ruleGenerators
                            .get(rule.getClass());
            if (ruleGenerator != null) {
                final Map<String, Object> ruleMap = ruleGenerator.generate(rule, locale);
                if (ruleMap != null) {
                    map.putAll(ruleMap);
                }
            }
        }
        return JsonUtil.map2Json(map).replace('"', '\'');
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final Map<String, ValidationMapGenerator> beans = context
                        .getBeansOfType(ValidationMapGenerator.class);
        for (final ValidationMapGenerator<ValidationRule> ruleGenerator : beans.values()) {
            final Class<?> ruleClass = ClassUtil.getActualGenericType(ruleGenerator.getClass(),
                            ValidationMapGenerator.class, 0);
            this.ruleGenerators.put(ruleClass, ruleGenerator);
        }
    }

}
