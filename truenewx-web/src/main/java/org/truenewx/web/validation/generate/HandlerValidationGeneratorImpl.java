package org.truenewx.web.validation.generate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.core.Strings;
import org.truenewx.data.model.Model;
import org.truenewx.data.validation.config.ValidationConfiguration;
import org.truenewx.data.validation.config.ValidationConfigurationFactory;
import org.truenewx.data.validation.rule.ValidationRule;

/**
 * 处理器校验生成器实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component("handlerValidationGenerator")
public class HandlerValidationGeneratorImpl implements HandlerValidationGenerator {
    /**
     * 传输模型校验属性名称.
     */
    private static final String VALIDATION_ATTRIBUTE_NAME = "validation";
    /**
     * 类名关键字
     */
    private static final String CLASS_NAME = "@type";

    private ValidationGenerator validationGenerator;
    private ValidationConfigurationFactory validationConfigurationFactory;

    @Autowired(required = false)
    public void setValidationGenerator(final ValidationGenerator validationGenerator) {
        this.validationGenerator = validationGenerator;
    }

    @Autowired(required = false)
    public void setValidationConfigurationFactory(
                    final ValidationConfigurationFactory validationConfigurationFactory) {
        this.validationConfigurationFactory = validationConfigurationFactory;
    }

    @Override
    public void generate(final HttpServletRequest request,
                    final Class<? extends Model>[] modelClasses, final ModelAndView mav) {
        if (this.validationGenerator != null && this.validationConfigurationFactory != null
                        && modelClasses.length > 0 && mav != null) {
            final Map<String, Map<String, String>> validations = new HashMap<String, Map<String, String>>();
            for (final Class<? extends Model> modelClass : modelClasses) {
                final ValidationConfiguration configuration = this.validationConfigurationFactory
                                .getConfiguration(modelClass);
                if (configuration != null) {
                    final Set<String> propertyNames = configuration.getPropertyNames();
                    if (propertyNames.size() > 0) {
                        final Map<String, String> propertyExpressionMap = new HashMap<String, String>();
                        propertyExpressionMap.put(CLASS_NAME, modelClass.getName());
                        for (final String propertyName : propertyNames) {
                            final Set<ValidationRule> rules = configuration.getRules(propertyName);
                            final String expression = this.validationGenerator
                                            .generateExpression(rules, request.getLocale());
                            if (expression != null) {
                                propertyExpressionMap.put(propertyName, expression);
                            }
                        }
                        if (propertyExpressionMap.size() > 0) {
                            final String className = modelClass.getSimpleName();
                            final Map<String, String> oldMap = validations.put(className,
                                            propertyExpressionMap);
                            if (oldMap != null) {
                                // 如果存在重复的类简名，则移除以类简名为关键字的表达式，使用类全名关键字
                                validations.remove(className);
                                validations.put(oldMap.get(CLASS_NAME), oldMap);
                                validations.put(propertyExpressionMap.get(CLASS_NAME),
                                                propertyExpressionMap);
                            }
                        }
                    }
                }
            }
            final Map<String, String> validation = new HashMap<>();
            if (validations.size() > 0) {
                // 添加第一个模型类的校验属性映射
                validation.putAll(validations.values().iterator().next());

                if (validations.size() > 1) { // 多个模型类时，添加模型类的属性名加上类名前缀的校验属性映射
                    for (final Entry<String, Map<String, String>> entry : validations.entrySet()) {
                        final String className = entry.getKey();
                        for (final Entry<String, String> e : entry.getValue().entrySet()) {
                            String propertyName = e.getKey();
                            if (!CLASS_NAME.equals(propertyName)) {
                                propertyName = StringUtils.join(className, Strings.DOT,
                                                propertyName); // 属性名加上类名前缀
                                validation.put(propertyName, e.getValue());
                            }
                        }
                    }
                }
            }
            mav.addObject(VALIDATION_ATTRIBUTE_NAME, validation);
        }
    }

}
