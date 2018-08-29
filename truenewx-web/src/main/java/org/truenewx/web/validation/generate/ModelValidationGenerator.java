package org.truenewx.web.validation.generate;

import java.util.Locale;
import java.util.Map;

import org.truenewx.data.model.Model;

/**
 * 模型校验规则生成器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface ModelValidationGenerator {

    Map<String, Map<String, Object>> generate(Class<? extends Model> modelClass, Locale locale);

    Map<String, Object> generate(Class<? extends Model> modelClass, Locale locale,
            String propertyName);

}
