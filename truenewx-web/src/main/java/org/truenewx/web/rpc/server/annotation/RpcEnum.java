package org.truenewx.web.rpc.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC枚举
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcEnum {
    /**
     *
     * @return 枚举类型
     */
    Class<? extends Enum<?>> type();

    /**
     * 不指定子类型名称，则没必要使用本注解
     * 
     * @return 子类型名称
     */
    String sub();
}
