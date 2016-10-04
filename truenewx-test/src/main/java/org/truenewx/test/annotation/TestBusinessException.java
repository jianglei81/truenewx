package org.truenewx.test.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试业务异常
 * 
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestBusinessException {

    /**
     * 期望的错误码集
     * 
     * @return 期望的错误码集
     */
    String[] value();

}
