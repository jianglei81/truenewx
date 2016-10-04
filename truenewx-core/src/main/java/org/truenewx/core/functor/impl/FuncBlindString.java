package org.truenewx.core.functor.impl;

import com.google.common.base.Function;

/**
 * 函数：致盲字符串
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class FuncBlindString implements Function<String, String> {
    /**
     * 单例
     */
    public static final FuncBlindString INSTANCE = new FuncBlindString();

    private FuncBlindString() {
    }

    @Override
    public String apply(final String s) {
        return s;
    }

}
