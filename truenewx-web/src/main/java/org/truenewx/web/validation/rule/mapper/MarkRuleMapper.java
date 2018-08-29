package org.truenewx.web.validation.rule.mapper;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.springframework.stereotype.Component;
import org.truenewx.core.util.StringUtil;
import org.truenewx.data.validation.rule.MarkRule;

/**
 * 标识规则的校验映射集生成器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class MarkRuleMapper implements ValidationRuleMapper<MarkRule> {
    private Map<Class<?>, String> annotationTypeMapping = new HashMap<>();

    public MarkRuleMapper() {
        this.annotationTypeMapping.put(NotEmpty.class, "required");
        this.annotationTypeMapping.put(URL.class, "url");
    }

    @Override
    public Map<String, Object> toMap(final MarkRule rule, final Locale locale) {
        final Map<String, Object> result = new HashMap<String, Object>();
        for (final Class<? extends Annotation> annotationType : rule.getAnnotationTypes()) {
            String name = this.annotationTypeMapping.get(annotationType);
            if (name == null) { // 默认以注解类型简单名首字母小写为校验规则名称
                name = StringUtil.firstToLowerCase(annotationType.getSimpleName());
            }
            result.put(name, Boolean.TRUE);
        }
        return result;
    }

}
