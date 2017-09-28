package org.truenewx.web.spring.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;
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
}
