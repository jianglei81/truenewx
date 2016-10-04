package org.truenewx.web.rpc.serializer;

import org.springframework.stereotype.Component;
import org.truenewx.core.serializer.JsonSerializer;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;

import com.alibaba.fastjson.JSON;

/**
 * JSON-RPC序列化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class JsonRpcSerializer extends JsonSerializer implements RpcSerializer {

    @Override
    public String serializeBean(final Object bean, final RpcResultFilter[] filters) {
        if (bean != null) {
            if (filters.length > 0) { // RPC结果有特殊设置，则启用过滤器
                return JSON.toJSONString(bean, new JsonRpcResultPropertyPreFilter(filters));
            } else {
                return JSON.toJSONString(bean);
            }
        }
        return JSON.toJSONString(null);
    }
}