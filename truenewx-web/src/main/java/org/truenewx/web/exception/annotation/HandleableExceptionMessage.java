package org.truenewx.web.exception.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注方法的可处理异常以错误消息形式处理异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface HandleableExceptionMessage {

    /**
     *
     * @return 是否返回表示业务异常的Response错误状态码，默认为true
     */
    boolean respondErrorStatus() default true;

}
