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
import javax.validation.ReportAsSingleViolation;

import org.truenewx.data.validation.constraint.validator.NotContainsHtmlCharsValidator;

/**
 * 不能包含HTML字符，这些特殊字符可能破坏HTML文档的结构
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Target({ FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
@Inherited
@NotContains({ "<", ">", "\"", "'" })
@ReportAsSingleViolation
@Constraint(validatedBy = NotContainsHtmlCharsValidator.class)
public @interface NotContainsHtmlChars {

    String message() default NotContains.DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
