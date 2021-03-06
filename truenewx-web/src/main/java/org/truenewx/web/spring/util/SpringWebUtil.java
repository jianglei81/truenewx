package org.truenewx.web.spring.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.spring.context.SpringWebContext;

/**
 * Spring Web工具类
 *
 * @author jianglei
 *
 */
public class SpringWebUtil {

    private SpringWebUtil() {
    }

    /**
     * 获取web项目应用范围内的ApplicationContext实例
     *
     * @param pageContext
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext(final PageContext pageContext) {
        final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        return RequestContextUtils.findWebApplicationContext(request,
                pageContext.getServletContext());
    }

    /**
     * 获取web项目应用范围内的ApplicationContext实例
     *
     * @param request
     *            HTTP请求
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext(final HttpServletRequest request) {
        try {
            return RequestContextUtils.findWebApplicationContext(request);
        } catch (final IllegalStateException e) {
            return null;
        }
    }

    /**
     * 获取web项目应用范围内的ApplicationContext实例
     *
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext() {
        return getApplicationContext(SpringWebContext.getRequest());
    }

    /**
     * 先尝试从Spring的LocaleResolver中获取区域，以便以自定义的方式获取区域
     *
     * @param request
     *            请求
     * @return 区域
     */
    public static Locale getLocale(final HttpServletRequest request) {
        final LocaleResolver localeResolver = SpringUtil
                .getFirstBeanByClass(getApplicationContext(request), LocaleResolver.class);
        if (localeResolver != null) {
            return localeResolver.resolveLocale(request);
        } else {
            return request.getLocale();
        }
    }
}
