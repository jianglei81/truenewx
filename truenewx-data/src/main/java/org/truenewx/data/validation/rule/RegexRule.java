package org.truenewx.data.validation.rule;

/**
 * 正则表达式规则
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class RegexRule extends ValidationRule {
    /**
     * 正则表达式
     */
    private String expression;
    /**
     * 错误消息模板
     */
    private String message;

    /**
     * 用指定正则表达式构建
     * 
     * @param value
     *            正则表达式
     * @param message
     *            校验不通过时显示的错误消息模板
     */
    public RegexRule(final String expression, final String message) {
        this.expression = expression;
        this.message = message;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
