package org.truenewx.web.rpc.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.core.Strings;

/**
 * RPC方法
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcMethod {

    /**
     *
     * @return 参数配置集
     */
    RpcArg[] args() default {};

    /**
     * @return 结果过滤器
     */
    RpcResult result() default @RpcResult();

    /**
     *
     *
     * @return 方法说明
     */
    String caption() default Strings.EMPTY;

    /**
     *
     * @return 枚举限定
     */
    RpcEnum[] enums() default {};

    /**
     *
     * @return 适用的版本号，为空表示适用所有版本
     */
    String version() default Strings.EMPTY;

}
