package org.truenewx.web.rpc.server;

import org.truenewx.web.rpc.server.annotation.RpcResultFilter;

/**
 * RPC执行结果
 *
 * @author jianglei
 * @since JDK 1.8
 */
class RpcInvokeResult {
    private Object value;
    private RpcResultFilter[] filters;

    public RpcInvokeResult(final Object value, final RpcResultFilter... filters) {
        this.value = value;
        this.filters = filters;
    }

    public Object getValue() {
        return this.value;
    }

    public RpcResultFilter[] getFilters() {
        return this.filters;
    }

}
