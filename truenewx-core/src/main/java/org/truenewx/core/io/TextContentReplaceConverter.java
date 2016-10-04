package org.truenewx.core.io;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 文本内容替换转换器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TextContentReplaceConverter implements TextContentConverter {

    private Map<String, String> replacement;

    public TextContentReplaceConverter(final Map<String, String> replacement) {
        this.replacement = replacement;
    }

    @Override
    public String convert(String content) {
        for (final Entry<String, String> entry : this.replacement.entrySet()) {
            content = content.replaceAll(entry.getKey(), entry.getValue());
        }
        return content;
    }

}
