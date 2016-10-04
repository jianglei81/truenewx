package org.truenewx.web.rpc.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC结果过滤
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcResultFilter {
    /**
     * @return 过滤类型
     */
    Class<?> type();

    /**
     * @return 需包含的属性名称集，为空时表示全包含
     */
    String[] includes() default {};

    /**
     * @return 需排除的属性名称集，为空时表示不排除任何属性
     */
    String[] excludes() default {};
}
