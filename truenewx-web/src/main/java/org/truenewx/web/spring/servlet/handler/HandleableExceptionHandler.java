package org.truenewx.web.spring.servlet.handler;

import org.springframework.web.servlet.ModelAndView;
import org.truenewx.core.exception.HandleableException;

/**
 * 可处理异常的处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface HandleableExceptionHandler {
    /**
     * 用指定处理器名称处理指定异常
     *
     * @param methodName
     *            抛出异常的方法名
     * @param he
     *            待处理的异常
     * @param mav
     *            处理异常时的模型和视图
     */
    void handleException(String methodName, HandleableException he, ModelAndView mav);

}
