package org.truenewx.core.enums.support.functor;

import com.google.common.base.Enums;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.annotation.Caption;
import org.truenewx.core.annotation.Name;
import org.truenewx.core.enums.annotation.EnumSub;
import org.truenewx.core.enums.support.EnumItem;
import org.truenewx.core.enums.support.EnumType;
import org.truenewx.core.functor.TripleFunction;
import org.truenewx.core.util.CaptionUtil;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * 函数：从枚举类构建默认枚举类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FuncBuildDefaultEnumType extends TripleFunction<Class<?>, String, Locale, EnumType> {
    /**
     * 单实例
     */
    public static FuncBuildDefaultEnumType INSTANCE = new FuncBuildDefaultEnumType();

    private FuncBuildDefaultEnumType() {
    }

    @Override
    public EnumType apply(Class<?> enumClass, String subname, Locale locale) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(enumClass.getName() + " is not an enum");
        }
        EnumType enumType = newEnumType(enumClass, subname);
        for (Enum<?> enumConstant : (Enum<?>[]) enumClass.getEnumConstants()) {
            Field field = Enums.getField(enumConstant);
            if (matchesSub(field, subname)) {
                String caption = CaptionUtil.getCaption(field, locale);
                if (caption == null) { // 默认用枚举常量名称作为显示名
                    caption = enumConstant.name();
                }
                enumType.addItem(new EnumItem(enumConstant.ordinal(), enumConstant.name(), caption));
            }
        }
        return enumType;
    }

    private boolean matchesSub(Field field, String subname) {
        if (subname == null) {
            return true;
        }
        EnumSub enumSub = field.getAnnotation(EnumSub.class);
        return enumSub != null && ArrayUtils.contains(enumSub.value(), subname);
    }

    public String getEnumTypeName(Class<?> enumClass) {
        String typeName = null;
        Name name = enumClass.getAnnotation(Name.class);
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
     * @param enumClass 枚举类
     * @param subname   子类型名称
     * @return 不含枚举项目的枚举类型对象
     */
    private EnumType newEnumType(Class<?> enumClass, String subname) {
        String typeCaption = null;
        Caption captionAnno = enumClass.getAnnotation(Caption.class);
        if (captionAnno != null) {
            typeCaption = captionAnno.value();
        }
        // 默认使用枚举类型简称为显示名称
        if (StringUtils.isBlank(typeCaption)) {
            typeCaption = enumClass.getSimpleName();
        }
        String typeName = getEnumTypeName(enumClass);
        return new EnumType(typeName, subname, typeCaption);
    }
}
