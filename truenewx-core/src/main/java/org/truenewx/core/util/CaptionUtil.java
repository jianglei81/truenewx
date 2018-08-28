package org.truenewx.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.annotation.Caption;

/**
 * 显示名称工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class CaptionUtil {

    private CaptionUtil() {
    }

    private static Caption getCaptionAnnonation(Caption[] captionAnnonations, Locale locale) {
        Caption defaultCaptionAnnonation = null;
        for (Caption captionAnnonation : captionAnnonations) {
            if (StringUtils.isBlank(captionAnnonation.locale())) {
                // 暂存默认语言的Caption注解
                defaultCaptionAnnonation = captionAnnonation;
            } else if (locale.toString().equals(captionAnnonation.locale())) {
                // 找到语言匹配的Caption注解
                return captionAnnonation;
            }
        }
        // 找不到语言匹配的Caption注解，则返回默认语言的Caption注解
        return defaultCaptionAnnonation;
    }

    public static String getCaption(Class<?> clazz, Locale locale) {
        Caption[] captionAnnonations = clazz.getAnnotationsByType(Caption.class);
        Caption captionAnnonation = getCaptionAnnonation(captionAnnonations, locale);
        return captionAnnonation == null ? null : captionAnnonation.value();
    }

    public static String getCaption(Method method, Locale locale) {
        Caption[] captionAnnonations = method.getAnnotationsByType(Caption.class);
        Caption captionAnnonation = getCaptionAnnonation(captionAnnonations, locale);
        return captionAnnonation == null ? null : captionAnnonation.value();
    }

    public static String getCaption(Parameter parameter, Locale locale) {
        Caption[] captionAnnonations = parameter.getAnnotationsByType(Caption.class);
        Caption captionAnnonation = getCaptionAnnonation(captionAnnonations, locale);
        return captionAnnonation == null ? null : captionAnnonation.value();
    }

    public static String getCaption(Field field, Locale locale) {
        Caption[] captionAnnonations = field.getAnnotationsByType(Caption.class);
        Caption captionAnnonation = getCaptionAnnonation(captionAnnonations, locale);
        return captionAnnonation == null ? null : captionAnnonation.value();
    }

}
