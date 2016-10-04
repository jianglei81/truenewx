package org.truenewx.core.functor.impl;

import java.util.Date;

import org.truenewx.core.util.DateUtil;

import com.google.common.base.Function;

/**
 * 函数：格式化日期
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class FuncFormatDate implements Function<Date, String> {
    /**
     * 长日期格式的日期格式化函数实例
     */
    public static FuncFormatDate LONG = new FuncFormatDate(DateUtil.LONG_DATE_PATTERN);
    /**
     * 短日期格式的日期格式化函数实例
     */
    public static FuncFormatDate SHORT = new FuncFormatDate(DateUtil.SHORT_DATE_PATTERN);

    private String pattern;

    /**
     * @param pattern
     *            格式
     */
    public FuncFormatDate(final String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Override
    public String apply(final Date date) {
        return DateUtil.format(date, this.pattern);
    }

}
