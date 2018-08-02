package org.truenewx.core.parser.util;

import org.truenewx.core.Strings;
import org.truenewx.core.util.DateUtil;

import freemarker.template.Configuration;

/**
 * FreeMarker工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FreeMarkerUtil {

    private FreeMarkerUtil() {
    }

    public static Configuration getDefaultConfiguration() {
        final Configuration config = new Configuration();
        applyDefault(config);
        return config;
    }

    public static void applyDefault(final Configuration config) {
        config.setClassicCompatible(true);
        config.setNumberFormat("0.##");
        config.setTimeFormat(DateUtil.TIME_PATTERN);
        config.setDateFormat(DateUtil.SHORT_DATE_PATTERN);
        config.setDateTimeFormat(DateUtil.LONG_DATE_PATTERN);
        config.clearEncodingMap();
        config.setDefaultEncoding(Strings.ENCODING_UTF8);
    }

}
