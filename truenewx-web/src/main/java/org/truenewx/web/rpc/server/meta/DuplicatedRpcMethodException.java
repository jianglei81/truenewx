package org.truenewx.web.rpc.server.meta;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;

/**
 * 重名RPC方法异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DuplicatedRpcMethodException extends RpcException {

    private static final long serialVersionUID = -8714547397267622760L;

    public DuplicatedRpcMethodException(Class<?> clazz, String methodName, Integer argCount) {
        super(StringUtils.join(clazz.getSimpleName(), Strings.DOT, methodName, "(",
                getArgExpression(argCount), ") method, maybe wrong number of arguments"));
    }

    private static String getArgExpression(Integer argCount) {
        StringBuffer result = new StringBuffer();
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
