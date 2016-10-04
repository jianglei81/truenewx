package org.truenewx.web.rpc.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;
import org.truenewx.core.Strings;

/**
 * RPC控制器，继承自Spring的@Controller，标识的类具有MVC控制器的特征
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface RpcController {
    /**
     * The value may indicate a suggestion for a logical component name, to be turned into a Spring
     * bean in case of an autodetected component.
     *
     * @return the suggested component name, if any
     */
    String value() default Strings.EMPTY;

    /**
     * 综述当前RPC控制器的作用，便于生成更好的API文档
     *
     * @return 说明
     */
    String caption() default Strings.EMPTY;

    /**
     * 指定所属业务模块，便于生成更好的生成API文档
     * 
     * @return 所属业务模块
     */
    String module() default Strings.EMPTY;
}
