package org.truenewx.core.functor.impl;

import java.util.Date;

import org.truenewx.core.util.DateUtil;

import com.google.common.base.Function;

/**
 * 函数：解析字符串为日期
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class FuncParseDate implements Function<String, Date> {
    /**
     * 长日期格式的日期解析函数实例
     */
    public static FuncParseDate LONG = new FuncParseDate(DateUtil.LONG_DATE_PATTERN);
    /**
     * 短日期格式的日期解析函数实例
     */
    public static FuncParseDate SHORT = new FuncParseDate(DateUtil.SHORT_DATE_PATTERN);

    private String pattern;

    /**
     * @param pattern
     *            格式
     */
    public FuncParseDate(final String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Override
    public Date apply(final String s) {
        return DateUtil.parse(s.trim(), this.pattern);
    }

}
