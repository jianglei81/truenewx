package org.truenewx.web.rpc.util;

import org.truenewx.core.enums.NullEnum;
import org.truenewx.web.menu.util.MenuUtil;
import org.truenewx.web.rpc.server.annotation.RpcMethod;

/**
 * RPC工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcUtil {

    private RpcUtil() {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String getAuthority(final RpcMethod rpcMethod) {
        if (rpcMethod != null) {
            final Class<?> type = rpcMethod.authType();
            if (type != NullEnum.class) {
                final Enum<?> enumConstant = Enum.valueOf((Class<Enum>) type, rpcMethod.auth());
                return MenuUtil.getAuthority(enumConstant);
            } else {
                return rpcMethod.auth();
            }
        }
        return null;
    }

}
