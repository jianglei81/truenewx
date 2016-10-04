package org.truenewx.web.spring.servlet.handler;

/**
 * 业务错误模型
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class BusinessError {
    private String code;
    private String message;
    private String field;

    public BusinessError(final String code, final String message, final String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    public BusinessError(final String code, final String message) {
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
