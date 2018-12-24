package org.truenewx.web.rpc.server.meta;

/**
 * RPC异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcException extends Exception {

    private static final long serialVersionUID = 6603816684269372970L;

    public RpcException(String message) {
        super(message);
    }

}
