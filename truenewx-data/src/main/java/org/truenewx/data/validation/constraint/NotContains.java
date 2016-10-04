package org.truenewx.data.validation.constraint;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.truenewx.data.validation.constraint.validator.NotContainsValidator;

/**
 * 不能包含字符串约束<br/>
 * 注意：不支持限制空格
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
@Inherited
@Constraint(validatedBy = NotContainsValidator.class)
public @interface NotContains {
    /**
     * @return 不能包含的字符串集，其中如有空格会被忽略
     */
    String[] value();

    String message() default "{org.truenewx.data.validation.constraint.NotContains.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
