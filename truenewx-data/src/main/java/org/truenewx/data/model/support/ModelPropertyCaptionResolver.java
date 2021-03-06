package org.truenewx.data.model.support;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.truenewx.core.i18n.PropertyCaptionResolver;
import org.truenewx.core.util.CaptionUtil;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.data.model.Model;
import org.truenewx.data.model.TransportModel;
import org.truenewx.data.validation.config.annotation.InheritConstraint;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * 模型属性显示名称解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class ModelPropertyCaptionResolver implements PropertyCaptionResolver {

    @Autowired
    private MessageSource messageSource;

    @Override
    public String resolveCaption(Class<?> clazz, String propertyName, Locale locale) {
        String caption = getCaption(clazz, propertyName, locale);
        if (caption == null) {
            // 如果从指定类型中无法获取属性显示名称，而指定类型又是提交模型或视图模型，则尝试从关联的实体模型中获取
            Class<?> entityClass = null;
            if (TransportModel.class.isAssignableFrom(clazz)) {
                entityClass = ClassUtil.getActualGenericType(clazz, TransportModel.class, 0);
            }
            if (entityClass != null) {
                caption = getCaption(entityClass, propertyName, locale);
            }
            // 如果从关联实体模型中仍然无法获取，则尝试从@InheritConstraint注解的关联实体和属性上获取
            if (caption == null) {
                InheritConstraint ic = ClassUtil.findAnnotation(clazz, propertyName,
                        InheritConstraint.class);
                if (ic != null) {
                    if (StringUtils.isNotBlank(ic.value())) {
                        propertyName = ic.value();
                    }
                    if (ic.type() != Model.class) {
                        entityClass = ic.type();
                    }
                    caption = getCaption(entityClass, propertyName, locale);
                }
            }
        }
        return caption;
    }

    private String getCaption(Class<?> clazz, String propertyName, Locale locale) {
        Field field = ClassUtil.findField(clazz, propertyName);
        if (field != null) {
            String caption = CaptionUtil.getCaption(field, locale);
            if (StringUtils.isEmpty(caption)) {
                caption = getPropertyPathCaption(ClassUtil.getFullPropertyPath(clazz, propertyName),
                        locale); // 先尝试取完全路径的
                if (caption == null) {
                    String simplePropertyPath = ClassUtil.getSimplePropertyPath(clazz,
                            propertyName);
                    caption = getPropertyPathCaption(simplePropertyPath, locale); // 再尝试取简短路径的
                    if (caption == null) {
                        caption = getPropertyPathCaption(propertyName, locale); // 最后尝试取仅属性的
                    }
                }
            }
            return caption;
        }
        return null;
    }

    private String getPropertyPathCaption(String propertyPath, Locale locale) {
        String text = this.messageSource.getMessage(propertyPath, null, null, locale);
        return propertyPath.equals(text) ? null : text;
    }

}
