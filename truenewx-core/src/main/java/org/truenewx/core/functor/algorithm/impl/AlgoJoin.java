package org.truenewx.core.functor.algorithm.impl;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.functor.algorithm.Algorithm;

import com.google.common.base.Function;

/**
 * 算法：集合批凑成字符串
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class AlgoJoin implements Algorithm {

    private AlgoJoin() {
    }

    public static <T> String visit(final T[] array, String separator,
                    @Nullable final Function<T, String> funcToString) {
        if (array == null) {
            return null;
        }
        if (funcToString == null) {
            return StringUtils.join(array, separator);
        }
        if (separator == null) {
            separator = Strings.EMPTY;
        }
        final StringBuffer result = new StringBuffer();
        for (final T obj : array) {
            final String s = funcToString.apply(obj);
            if (s != null) {
                result.append(s).append(separator);
            }
        }
        if (result.length() > 0) {
            result.delete(result.length() - separator.length(), result.length());
        }
        return result.toString();
    }

    public static <T> String visit(final Iterable<T> iterable, String separator,
                    @Nullable final Function<T, String> funcToString) {
        if (funcToString == null) {
            return StringUtils.join(iterable, separator);
        }
        if (separator == null) {
            separator = Strings.EMPTY;
        }
        final StringBuffer result = new StringBuffer();
        for (final T obj : iterable) {
            result.append(funcToString.apply(obj)).append(separator);
        }
        if (result.length() > 0) {
            result.delete(result.length() - separator.length(), result.length());
        }
        return result.toString();
    }

}
