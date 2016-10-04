package org.truenewx.data.validation.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.truenewx.data.model.Model;
import org.truenewx.data.validation.rule.ValidationRule;

/**
 * 校验设置
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class ValidationConfiguration {
    /**
     * 模型类
     */
    private Class<? extends Model> modelClass;
    /**
     * 属性名-约束集的映射
     */
    private Map<String, Set<ValidationRule>> ruleMapping;

    public ValidationConfiguration(final Class<? extends Model> modelClass) {
        this.modelClass = modelClass;
        this.ruleMapping = new HashMap<String, Set<ValidationRule>>();
    }

    public Class<? extends Model> getModelClass() {
        return this.modelClass;
    }

    public synchronized void addRule(final String propertyName,
                    @Nullable final ValidationRule rule) {
        if (rule != null) {
            getRules(propertyName).add(rule);
        }
    }

    public synchronized Set<ValidationRule> getRules(final String propertyName) {
        Set<ValidationRule> rules = this.ruleMapping.get(propertyName);
        if (rules == null) {
            rules = new LinkedHashSet<ValidationRule>(); // 保持规则加入的顺序
            this.ruleMapping.put(propertyName, rules);
        }
        return rules;
    }

    @SuppressWarnings("unchecked")
    public synchronized <R extends ValidationRule> R getRule(final String propertyName,
                    final Class<R> ruleClass) {
        final Set<ValidationRule> rules = getRules(propertyName);
        for (final ValidationRule rule : rules) {
            if (rule.getClass() == ruleClass) {
                return (R) rule;
            }
        }
        return null;
    }

    public Set<String> getPropertyNames() {
        return this.ruleMapping.keySet();
    }
}
