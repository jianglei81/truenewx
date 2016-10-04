package org.truenewx.web.rpc.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.core.Strings;

/**
 * RPC参数
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcArg {
    /**
     * 如果一个RPC方法的其中一个参数设置了名称，则其它参数均需设置名称，以支持Map形式的访问参数
     *
     * @return 参数名称
     */
    String name() default Strings.EMPTY;

    /**
     *
     *
     * @return 参数说明
     */
    String caption() default Strings.EMPTY;

    /**
     * 当参数类型为集合或Map时有效，Map时指定值类型
     *
     * @return 集合中元素的类型
     */
    Class<?> componentType() default Object.class;

    /**
     * 仅对复合类型的参数有效
     *
     * @return 参数类中有效的属性名称集合
     */
    String[] includes() default {};

    /**
     * 仅对复合类型的参数有效
     * 
     * @return 参数类中无效的属性名称集合
     */
    String[] excludes() default {};
}
