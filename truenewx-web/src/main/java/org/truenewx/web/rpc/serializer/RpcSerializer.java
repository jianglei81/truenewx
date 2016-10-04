package org.truenewx.web.rpc.serializer;

import org.truenewx.core.serializer.StringSerializer;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;

/**
 * RPC序列化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface RpcSerializer extends StringSerializer {

    /**
     * 序列化Bean
     *
     * @param bean
     *            Bean
     * @param filters
     *            属性过滤集
     * @return 序列化后的字符串
     */
    String serializeBean(Object bean, RpcResultFilter[] filters);
}
