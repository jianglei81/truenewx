package org.truenewx.core.enums.support.functor;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.annotation.Caption;
import org.truenewx.core.annotation.Name;
import org.truenewx.core.enums.support.EnumItem;
import org.truenewx.core.enums.support.EnumType;

import com.google.common.base.Enums;
import com.google.common.base.Function;

/**
 * 函数：从枚举类构建默认枚举类型
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class FuncBuildDefaultEnumType implements Function<Class<?>, EnumType> {
    /**
     * 单实例
     */
    public static final FuncBuildDefaultEnumType INSTANCE = new FuncBuildDefaultEnumType();

    private FuncBuildDefaultEnumType() {
    }

    @Override
    public EnumType apply(final Class<?> enumClass) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(enumClass.getName() + " is not an enum");
        }
        final EnumType enumType = newEnumType(enumClass);
        for (final Enum<?> enumConstant : (Enum<?>[]) enumClass.getEnumConstants()) {
            final String caption;
            final Field field = Enums.getField(enumConstant);
            final Caption captionAnno = field.getAnnotation(Caption.class);
            if (captionAnno != null) {
                caption = captionAnno.value();
            } else { // 默认用枚举常量名称作为显示名
                caption = enumConstant.name();
            }
            enumType.addItem(new EnumItem(enumConstant.ordinal(), enumConstant.name(), caption));
        }
        return enumType;
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
