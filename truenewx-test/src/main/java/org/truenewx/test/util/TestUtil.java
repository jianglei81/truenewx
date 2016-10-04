package org.truenewx.test.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 单元测试工具类
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class TestUtil {
    /**
     * 是否单元测试环境中
     */
    private static Boolean TESTING;

    private TestUtil() {
    }

    /**
     * 
     * @return 是否单元测试环境中
     * 
     * @author jianglei
     */
    public static boolean isTesting() {
        if (TESTING == null) {
            final StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
            for (final StackTraceElement stackTrace : stackTraces) {
                final String stackString = stackTrace.toString();
                if (stackString.indexOf("junit.runners") >= 0) {
                    TESTING = true;
                    return TESTING;
                }
            }
            TESTING = false;
        }
        return TESTING;
    }

    /**
     * 用指定HTTP请求模拟Spring的HTTP请求
     * 
     * @param request
     *            HTTP请求
     */
    public static void mockSpringRequest(final HttpServletRequest request) {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
