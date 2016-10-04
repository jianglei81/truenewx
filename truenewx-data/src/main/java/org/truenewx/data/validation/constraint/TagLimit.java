package org.truenewx.data.validation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.truenewx.data.validation.constraint.validator.TagLimitValidator;

/**
 * 标签限定
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TagLimitValidator.class)
public @interface TagLimit {

    /**
     * 允许的标签名称清单
     *
     * @return 允许的标签名称清单
     */
    String[] allowed() default {};

    /**
     * 禁止的标签名称清单
     *
     * @return 禁止的标签名称清单
     */
    String[] forbidden() default {};

    String message() default "{org.truenewx.data.validation.constraint.TagLimit.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
