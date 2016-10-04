package org.truenewx.web.menu.model;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.web.rpc.RpcPort;

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

    /**
     *
     * @param auth
     *            权限
     * @param caption
     *            菜单说明
     */
    public MenuOperation(final String auth, final String caption) {
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
    public String getAuth(final String beanId, final String methodName, final Integer argCount) {
        if (contains(beanId, methodName, argCount)) {
            return getAuth();
        }
        return null;
    }

}
