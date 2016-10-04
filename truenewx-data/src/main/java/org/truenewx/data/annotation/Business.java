package org.truenewx.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注类、方法、构造函数具有业务逻辑<br/>
 * 一般用于原则上不应该具有业务逻辑的地方，由于某些原因具有了业务逻辑，标注出来以便于维护
 * 
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Business {
    /**
     * 描述说明
     * 
     * @return 描述说明
     */
    String desc() default "";
}
