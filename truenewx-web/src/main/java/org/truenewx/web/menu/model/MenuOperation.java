package org.truenewx.web.menu.model;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.web.rpc.RpcPort;
import org.truenewx.web.security.authority.Authority;

/**
 * 菜单操作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuOperation extends MenuAction {

    private static final long serialVersionUID = 5722461876992145272L;

    /**
     * RPC端口集合
     */
    private List<RpcPort> rpcs = new ArrayList<>();

    public MenuOperation(final Authority auth, final String caption) {
        super(auth, caption);
    }

    /**
     * RPC端口集合
     */
    public List<RpcPort> getRpcs() {
        return this.rpcs;
    }

    /**
     * 判断是否包含指定RPC
     *
     * @param beanId
     *            Bean Id
     * @param methodName
     *            方法名
     * @param argCount
     *            参数个数，为null则忽略参数个数
     * @return 是否包含指定RPC
     */
    public boolean contains(final String beanId, final String methodName, final Integer argCount) {
        for (final RpcPort rpc : this.rpcs) {
            if (rpc.isMatched(beanId, methodName, argCount)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Authority getAuth(final String beanId, final String methodName, final Integer argCount) {
        if (contains(beanId, methodName, argCount)) {
            return getAuth();
        }
        return null;
    }

}
