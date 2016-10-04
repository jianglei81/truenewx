package org.truenewx.core.functor.algorithm.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.functor.algorithm.Algorithm;

import com.google.common.base.Function;

/**
 * 算法：分割字符串
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class AlgoSplit implements Algorithm {

    private AlgoSplit() {
    }

    public static <T> List<T> visit(final String s, final String regex,
                    final Function<String, T> funcParseString) {
        final List<T> list = new ArrayList<>();
        if (StringUtils.isNotBlank(s)) {
            final String[] array = s.split(regex);
            for (final String str : array) {
                final T obj = funcParseString.apply(str);
                if (obj != null) {
                    list.add(obj);
                }
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] visit(final String s, final String regex,
                    final Function<String, T> funcParseString, final Class<T> elementClass) {
        if (StringUtils.isNotBlank(s)) {
            final String[] array = s.split(regex);
            final T[] result = (T[]) Array.newInstance(elementClass, array.length);
            for (int i = 0; i < array.length; i++) {
                result[i] = funcParseString.apply(array[i]);
            }
            return result;
        }
        return (T[]) Array.newInstance(elementClass, 0);
    }

}
