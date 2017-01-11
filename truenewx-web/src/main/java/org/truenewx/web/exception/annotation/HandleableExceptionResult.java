package org.truenewx.web.exception.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.truenewx.core.Strings;
import org.truenewx.data.model.Model;
import org.truenewx.web.spring.servlet.handler.HandleableExceptionHandler;

/**
 * 标注方法的可处理异常处理结果视图
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleableExceptionResult {
    /**
     * 默认的错误视图名称
     */
    public static final String DEFAULT_VIEW = "error-global";

    /**
     * 表示前一个页面的视图名称
     */
    public static final String PREV_VIEW = "prev:";

    /**
     * @return 结果视图名称，默认为{@link HandleableExceptionResult#DEFAULT_VIEW} =
     *         "error-global"，也可以使用{@link HandleableExceptionResult#PREV_VIEW} =
     *         "prev:" 返回前一个页面
     */
    String value() default HandleableExceptionResult.DEFAULT_VIEW;

    /**
     * @return 异常处理完毕后要生成校验规则的模型类型集
     */
    Class<? extends Model>[] validate() default {};

    /**
     * @return 是否使用自定义的异常处理器，默认为false，当前控制器必须实现
     *         {@link HandleableExceptionHandler}以处理异常
     */
    boolean handler() default false;

    /**
     * @return 返回按钮地址
     */
    String back() default Strings.EMPTY;
}
