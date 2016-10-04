package org.truenewx.data.validation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.truenewx.data.validation.constraint.validator.NotContainsAngleBracketValidator;

/**
 * 不能包含尖括弧
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
@NotContains({ "<", ">" })
@ReportAsSingleViolation
@Constraint(validatedBy = NotContainsAngleBracketValidator.class)
public @interface NotContainsAngleBracket {

    String message() default "{org.truenewx.data.validation.constraint.NotContains.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
