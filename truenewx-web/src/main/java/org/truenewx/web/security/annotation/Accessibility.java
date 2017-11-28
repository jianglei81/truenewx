package org.truenewx.web.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可访问性
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Accessibility {
    /**
     * @return 是否匿名可访问
     */
    boolean anonymous() default false;

    /**
     * @return 是否只有局域网可访问
     */
    boolean lan() default false;
}
