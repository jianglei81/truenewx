package org.truenewx.core.spring.context;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * 基于资源包属性集的消息来源
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ReloadableResourceBundleMessagesSource extends ReloadableResourceBundleMessageSource
        implements MessagesSource {

    public ReloadableResourceBundleMessagesSource() {
    }

    /**
     * 用指定基本名集合构建
     *
     * @param basenames
     *            基本名集合
     */
    public ReloadableResourceBundleMessagesSource(final String... basenames) {
        setBasenames(basenames);
    }

    @Override
    public Map<String, String> getMessages(final Locale locale) {
        final Map<String, String> messages = new TreeMap<>();
        final Properties properties = getMergedProperties(locale).getProperties();
        for (final Entry<Object, Object> entry : properties.entrySet()) {
            messages.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return messages;
    }

    @Override
    public Map<String, String> getMessages(final Locale locale, final String prefix,
            final boolean resultContainsPrefix) {
        final Map<String, String> messages = new TreeMap<>();
        final int prefixLength = prefix.length();
        final Properties properties = getMergedProperties(locale).getProperties();
        for (final Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(prefix)) {
                if (!resultContainsPrefix) {
                    key = key.substring(prefixLength);
                }
                messages.put(key, entry.getValue().toString());
            }
        }
        return messages;
    }

}
