package org.truenewx.web.rpc.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.core.Strings;

/**
 * RPC结果
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcResult {
    /**
     *
     *
     * @return 结果说明
     */
    String caption() default Strings.EMPTY;

    /**
     * 当结果类型为集合或Map时有效，Map时指定值类型
     *
     * @return 集合中元素的类型
     */
    Class<?> componentType() default Object.class;

    /**
     *
     * @return 结果过滤设置
     */
    RpcResultFilter[] filter() default {};
}
