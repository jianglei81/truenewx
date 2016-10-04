package org.truenewx.web.rpc.serializer;

import org.truenewx.core.util.json.MultiPropertyPreFilter;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;

/**
 * JSON-RPC结果属性前置过滤器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JsonRpcResultPropertyPreFilter extends MultiPropertyPreFilter {

    public JsonRpcResultPropertyPreFilter(final RpcResultFilter... resultFilters) {
        for (final RpcResultFilter resultFilter : resultFilters) {
            addFilteredProperties(resultFilter.type(), resultFilter.includes(),
                            resultFilter.excludes());
        }
    }

}
