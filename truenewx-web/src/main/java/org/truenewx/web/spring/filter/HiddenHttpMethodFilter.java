package org.truenewx.web.spring.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 处理隐藏HTTP请求方法的过滤器，用于适配Tomcat8的BUG
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class HiddenHttpMethodFilter extends OncePerRequestFilter {

    public static final String METHOD_PARAM = org.springframework.web.filter.HiddenHttpMethodFilter.DEFAULT_METHOD_PARAM;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        final String paramValue = request.getParameter(METHOD_PARAM);
        if (HttpMethod.POST.name().equals(request.getMethod())
                && StringUtils.hasLength(paramValue)) {
            final HttpServletRequest wrapper = new HttpMethodRequestWrapper(request);
            filterChain.doFilter(wrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

        public HttpMethodRequestWrapper(final HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getMethod() {
            final Object paramValue = getRequest().getAttribute(METHOD_PARAM);
            final String method;
            if (paramValue instanceof String) {
                method = (String) paramValue;
            } else {
                method = getRequest().getParameter(METHOD_PARAM);
            }
            return method.toUpperCase(Locale.ENGLISH);
        }
    }

    /**
     * 为了处理Tomcat8不支持forward的PUT和DELETE请求的BUG，恢复隐藏的HTTP请求方法为POST——只有在POST情况下才可使用隐藏的HTTP请求方法
     *
     * @param request
     *            HTTP请求
     */
    public static void cleanHiddenHttpMethod(final HttpServletRequest request) {
        final String method = request.getMethod();
        if (method.equals(HttpMethod.PUT.name()) || method.equals(HttpMethod.DELETE.name())) {
            request.setAttribute(METHOD_PARAM, HttpMethod.POST.name());
        }
    }

}
