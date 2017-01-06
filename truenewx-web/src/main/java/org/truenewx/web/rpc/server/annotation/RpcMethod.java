package org.truenewx.web.rpc.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.core.Strings;
import org.truenewx.core.enums.NullEnum;

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
     * @return 是否只有局域网可访问
     */
    boolean lan() default false;

    /**
     *
     * @return 是否登录后才能访问
     */
    boolean logined() default true;

    /**
     *
     * @return 必须拥有的权限，为空时表示无权限限制
     */
    String auth() default Strings.EMPTY;

    /**
     * @return 权限枚举类型
     */
    Class<? extends Enum<?>> authType() default NullEnum.class;

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
}
