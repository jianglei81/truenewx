package org.truenewx.web.validation.generate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.web.validation.generate.annotation.ValidationGeneratable;

/**
 * 校验生成拦截器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
@Component("validationGenerateInterceptor")
public class ValidationGenerateInterceptor implements HandlerInterceptor {
    /**
     * 处理器校验生成器
     */
    private HandlerValidationGenerator handlerValidationGenerator;

    @Autowired
    public void setHandlerValidationGenerator(
            final HandlerValidationGenerator handlerValidationGenerator) {
        this.handlerValidationGenerator = handlerValidationGenerator;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            final HandlerMethod hm = (HandlerMethod) handler;
            final ValidationGeneratable vg = hm.getMethodAnnotation(ValidationGeneratable.class);
            if (vg != null) {
                this.handlerValidationGenerator.generate(request, vg.value(), modelAndView);
            }
        }
    }

    @Override
    public void afterCompletion(final HttpServletRequest request,
            final HttpServletResponse response, final Object handler, final Exception ex)
            throws Exception {
    }

}
