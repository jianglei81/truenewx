package org.truenewx.web.spring.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

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
        return RequestContextUtils.getWebApplicationContext(pageContext.getRequest(),
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
        final ServletContext servletContext = request.getSession().getServletContext();
        try {
            return RequestContextUtils.getWebApplicationContext(request, servletContext);
        } catch (final IllegalStateException e) {
            return null;
        }
    }
}
