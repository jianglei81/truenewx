package org.truenewx.data.validation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.truenewx.data.validation.constraint.validator.NotContainsSpecialCharsValidator;

/**
 * 不能包含特殊字符，这些特殊字符可能破坏页面结构或影响数据查询的准确性
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target(FIELD)
@Retention(RUNTIME)
@NotContainsHtmlChars
@NotContainsSqlChars
@ReportAsSingleViolation
@Constraint(validatedBy = NotContainsSpecialCharsValidator.class)
public @interface NotContainsSpecialChars {

    /**
     * 逗号有时会作为存储分隔符，成为不能包含的特殊字符
     * 
     * @return 能否包含逗号
     */
    boolean comma() default false;

    String message() default "{org.truenewx.data.validation.constraint.NotContains.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
