package org.truenewx.web.spring.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.truenewx.web.spring.context.request.ServletWebRequestAttributes;

/**
 * 请求响应上下文处理拦截器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RequestResponseContextHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) throws Exception {
        final ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (!(sra instanceof ServletWebRequest)) {
            final ServletWebRequest swr;
            if (sra == null) {
                swr = new ServletWebRequest(request, response);
                LocaleContextHolder.setLocale(request.getLocale());
            } else {
                swr = new ServletWebRequestAttributes(sra, response);
            }
            RequestContextHolder.setRequestAttributes(swr);
        }
        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(final HttpServletRequest request,
            final HttpServletResponse response, final Object handler, final Exception ex)
            throws Exception {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletWebRequestAttributes) {
            ((ServletWebRequestAttributes) attributes).requestCompleted();
        }
    }

}
