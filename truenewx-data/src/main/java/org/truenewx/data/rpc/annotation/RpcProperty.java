package org.truenewx.data.rpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.core.Strings;

/**
 * RPC属性
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcProperty {

    /**
     * @return 属性说明
     */
    String caption() default Strings.EMPTY;

    /**
     * 当属性类型为集合或Map时有效，Map时指定值类型
     *
     * @return 集合中元素的类型
     */
    Class<?> componentType() default Object.class;
}
