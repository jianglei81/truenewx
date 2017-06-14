package org.truenewx.core.spring.context.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.truenewx.core.spring.context.MessagesSource;

/**
 * 基于资源包属性集的消息来源
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ReloadableResourceBundleMessageSource
        extends org.springframework.context.support.ReloadableResourceBundleMessageSource
        implements MessagesSource {

    private static final String PROPERTIES_SUFFIX = ".properties";
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    public void setResourcePatternResolver(final ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public ReloadableResourceBundleMessageSource() {
    }

    /**
     * 用指定基本名集合构建
     *
     * @param basenames
     *            基本名集合
     */
    public ReloadableResourceBundleMessageSource(final String... basenames) {
        setBasenames(basenames);
    }

    @Override
    public void setBasenames(final String... basenames) {
        final List<String> list = new ArrayList<>();
        for (String basename : basenames) {
            basename = basename.trim();
            if (basename.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
                try {
                    final Resource[] resources = this.resourcePatternResolver
                            .getResources(basename + PROPERTIES_SUFFIX);
                    for (final Resource resource : resources) {
                        String path = resource.getURI().toString();
                        path = path.substring(0, path.length() - PROPERTIES_SUFFIX.length());
                        list.add(path);
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else {
                list.add(basename);
            }
        }
        super.setBasenames(list.toArray(new String[list.size()]));
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
