package org.truenewx.test.util;

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

}
