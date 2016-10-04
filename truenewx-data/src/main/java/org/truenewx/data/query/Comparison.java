package org.truenewx.data.query;

/**
 * 比较操作符
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public enum Comparison {
    /**
     * 等于
     */
    EQUAL,
    /**
     * 不等于
     */
    NOT_EQUAL,
    /**
     * like
     */
    LIKE,
    /**
     * not like
     */
    NOT_LIKE,
    /**
     * in
     */
    IN,
    /**
     * not in
     */
    NOT_IN,
    /**
     * 大于
     */
    GREATER,
    /**
     * 大于等于
     */
    GREATER_EQUAL,
    /**
     * 小于
     */
    LESS,
    /**
     * 小于等于
     */
    LESS_EQUAL,
    /**
     * 为空
     */
    IS_NULL,
    /**
     * 不为空
     */
    NOT_NULL;

    /**
     * @return 是否一元比较符
     */
    public boolean isUnary() {
        return this == IS_NULL || this == NOT_NULL;
    }

    /**
     * @return 是否多元比较符
     */
    public boolean isMultiple() {
        return this == IN || this == NOT_IN;
    }

    /**
     * 获取在查询语言中的字符串形式.
     * 
     * @return 在查询语言中的字符串形式
     */
    public String toQlString() {
        switch (this) {
        case NOT_EQUAL:
            return " <> ";
        case LIKE:
            return " like ";
        case NOT_LIKE:
            return " not like ";
        case IN:
            return " in ";
        case NOT_IN:
            return " not in ";
        case GREATER:
            return " > ";
        case GREATER_EQUAL:
            return " >= ";
        case LESS:
            return " < ";
        case LESS_EQUAL:
            return " <= ";
        case IS_NULL:
            return " is null";
        case NOT_NULL:
            return " is not null";
        default:
            return " = ";
        }
    }
}
