package org.truenewx.web.rpc.server.meta;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;

/**
 * 没有这个RPC方法的异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NoSuchRpcMethodException extends NoSuchMethodException {

    private static final long serialVersionUID = -4679521829522243856L;

    public NoSuchRpcMethodException(final Class<?> clazz, final String methodName,
                    final Integer argCount) {
        // 为避免可能的全包名暴露，只列出简单包名，这已经为开发人员定位错误提供了足够的帮助
        super(StringUtils.join(clazz.getSimpleName(), Strings.DOT, methodName, "(",
                        getArgExpression(argCount), ") method, maybe wrong number of arguments"));
    }

    private static String getArgExpression(final Integer argCount) {
        final StringBuffer result = new StringBuffer();
        if (argCount != null) {
            for (int i = 0; i < argCount; i++) {
                result.append(Strings.ASTERISK).append(Strings.COMMA).append(Strings.SPACE);
            }
            if (result.length() > 0) {
                result.delete(result.length() - 2, result.length());
            }
        }
        return result.toString();
    }

}
