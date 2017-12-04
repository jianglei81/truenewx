package org.truenewx.web.rpc.serializer;

import org.springframework.stereotype.Component;
import org.truenewx.core.serializer.JsonSerializer;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JSON-RPC序列化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class JsonRpcSerializer extends JsonSerializer implements RpcSerializer {

    private final SerializerFeature[] serializerFeatures = { SerializerFeature.DisableCircularReferenceDetect };

    @Override
    public String serializeBean(final Object bean, final RpcResultFilter[] filters) {
        if (bean != null) {
            if (filters.length > 0) { // RPC结果有特殊设置，则启用过滤器
                return JSON.toJSONString(bean, new JsonRpcResultPropertyPreFilter(filters), this.serializerFeatures);
            } else {
                return JSON.toJSONString(bean, this.serializerFeatures);
            }
        }
        return JSON.toJSONString(null);
    }
}