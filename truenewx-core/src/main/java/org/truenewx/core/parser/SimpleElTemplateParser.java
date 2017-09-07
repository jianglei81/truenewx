package org.truenewx.core.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.truenewx.core.util.BeanUtil;
import org.truenewx.core.util.StringUtil;

/**
 * 简单EL表达式模板的解析器。以简单方式解析模板内容中的EL表达式，生成实际内容
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SimpleElTemplateParser implements TemplateParser {
    private static final String REPLACE_KEY_PREFIX = "${";
    private static final String REPLACE_KEY_SUFFIX = "}";

    @Override
    public String parse(String templateContent, Map<String, ? extends Object> params,
            final Locale locale) {
        if (StringUtils.isEmpty(templateContent)) {
            return templateContent;
        }
        if (params == null) {
            params = new HashMap<>(0);
        }
        final String[] replaceContents = StringUtil.substringsBetweens(templateContent,
                REPLACE_KEY_PREFIX, REPLACE_KEY_SUFFIX);
        for (final String replaceContent : replaceContents) {
            try {
                final String replaceKey = replaceContent.substring(REPLACE_KEY_PREFIX.length(),
                        replaceContent.length() - REPLACE_KEY_SUFFIX.length());
                if (StringUtils.isEmpty(replaceKey)) {
                    continue;
                }
                final String[] propertyNames = replaceKey.split("\\.");
                if (propertyNames.length == 0) {
                    continue;
                }
                Object value = params.get(propertyNames[0]);
                if (propertyNames.length > 1) {
                    value = BeanUtil.getPropertyValue(value,
                            replaceKey.substring(propertyNames[0].length() + 1));
                } else {
                    final String[] replaces = StringUtil.substringsBetweens(value.toString(),
                            REPLACE_KEY_PREFIX, REPLACE_KEY_SUFFIX);
                    if (replaces.length > 0) {
                        value = parse(value.toString(), params, locale);
                    }
                }
                final String key = REPLACE_KEY_PREFIX + replaceKey + REPLACE_KEY_SUFFIX;
                templateContent = templateContent.replace(key,
                        value == null ? "" : value.toString());
            } catch (final Exception e) { // 忽略单个替换异常
                LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            }
        }
        return templateContent;
    }

    @Override
    public String parse(final File templateFile, final Map<String, ?> params, final Locale locale)
            throws IOException {
        final String templateContent = IOUtils.toString(new FileInputStream(templateFile));
        return parse(templateContent, params, locale);
    }

}
