package org.truenewx.data.validation.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.core.Strings;
import org.truenewx.data.model.Entity;

/**
 * 继承约束
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface InheritConstraint {
    /**
     * 被继承约束的对应实体的指定属性名称，默认为当前属性名称
     *
     * @return 被继承约束的对应实体的指定属性名称
     */
    String value() default Strings.EMPTY;

    /**
     * 被继承的实体类型
     *
     * @return 被继承的实体类型
     */
    Class<? extends Entity> type() default Entity.class;
}
