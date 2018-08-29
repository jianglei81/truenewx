package org.truenewx.web.exception.message;

import java.util.Locale;

import org.truenewx.core.exception.SingleException;

/**
 * 单异常消息解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SingleExceptionMessageResolver {
    /**
     * 解析指定单异常得到异常消息
     *
     * @param se
     *            单异常
     * @param locale
     *            区域
     * @return 异常消息
     */
    String resolveMessage(SingleException se, Locale locale);
}
