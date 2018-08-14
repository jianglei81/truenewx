package org.truenewx.web.spring.servlet.handler;

import javax.servlet.http.HttpServletResponse;

/**
 * 已处理的错误模型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class HandledError {
    /**
     * 响应状态码：已处理错误
     */
    public static final int SC_HANDLED_ERROR = HttpServletResponse.SC_CONFLICT; // 借用错误码：对当前资源状态，请求不能完成

    private String code;
    private String message;
    private String field;

    public HandledError(String code, String message, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public HandledError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getField() {
        return this.field;
    }

}
