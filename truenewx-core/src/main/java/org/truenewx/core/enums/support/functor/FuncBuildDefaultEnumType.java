package org.truenewx.core.enums.support.functor;

import java.lang.reflect.Field;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.annotation.Caption;
import org.truenewx.core.annotation.Name;
import org.truenewx.core.enums.support.EnumItem;
import org.truenewx.core.enums.support.EnumType;
import org.truenewx.core.functor.BinateFunction;

import com.google.common.base.Enums;

/**
 * 函数：从枚举类构建默认枚举类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FuncBuildDefaultEnumType extends BinateFunction<Class<?>, Locale, EnumType> {
    /**
     * 单实例
     */
    public static final FuncBuildDefaultEnumType INSTANCE = new FuncBuildDefaultEnumType();

    private FuncBuildDefaultEnumType() {
    }

    @Override
    public EnumType apply(final Class<?> enumClass, final Locale locale) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(enumClass.getName() + " is not an enum");
        }
        final EnumType enumType = newEnumType(enumClass);
        for (final Enum<?> enumConstant : (Enum<?>[]) enumClass.getEnumConstants()) {
            final String caption;
            final Field field = Enums.getField(enumConstant);
            final Caption captionAnnotation = getCaptionAnnotation(field, locale);
            if (captionAnnotation != null) {
                caption = captionAnnotation.value();
            } else { // 默认用枚举常量名称作为显示名
                caption = enumConstant.name();
            }
            enumType.addItem(new EnumItem(enumConstant.ordinal(), enumConstant.name(), caption));
        }
        return enumType;
    }

    private Caption getCaptionAnnotation(final Field field, final Locale locale) {
        final Caption[] captionAnnonations = field.getAnnotationsByType(Caption.class);
        Caption defaultCaptionAnnonation = null;
        for (final Caption captionAnnonation : captionAnnonations) {
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

    public String getEnumTypeName(final Class<?> enumClass) {
        String typeName = null;
        final Name name = enumClass.getAnnotation(Name.class);
        if (name != null) {
            typeName = name.value();
        }
        if (StringUtils.isBlank(typeName)) {
            typeName = enumClass.getName();
        }
        return typeName;
    }

    /**
     * 创建枚举类型对象，不含枚举项目
     *
     * @param enumClass
     *            枚举类
     * @return 不含枚举项目的枚举类型对象
     */
    private EnumType newEnumType(final Class<?> enumClass) {
        String typeCaption = null;
        final Caption captionAnno = enumClass.getAnnotation(Caption.class);
        if (captionAnno != null) {
            typeCaption = captionAnno.value();
        }
        // 默认使用枚举类型简称为显示名称
        if (StringUtils.isBlank(typeCaption)) {
            typeCaption = enumClass.getSimpleName();
        }
        final String typeName = getEnumTypeName(enumClass);
        return new EnumType(typeName, typeCaption);
    }
}
