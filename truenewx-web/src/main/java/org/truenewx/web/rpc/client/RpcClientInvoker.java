package org.truenewx.web.rpc.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.truenewx.core.Strings;
import org.truenewx.core.exception.AjaxException;
import org.truenewx.core.tuple.Binate;
import org.truenewx.core.util.ClientRequestSupport;
import org.truenewx.web.rpc.serializer.RpcSerializer;
import org.truenewx.web.spring.servlet.handler.HandledError;

/**
 * RPC客户端调用器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RpcClientInvoker extends ClientRequestSupport implements RpcClient {
    private String serverUrlRoot;
    private RpcSerializer serializer;

    /**
     * @param serverUrlRoot
     *            服务端URL根路径
     */
    public RpcClientInvoker(String serverUrlRoot) {
        this.serverUrlRoot = serverUrlRoot;
    }

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    private String getInvokeUrl(String beanId, String methodName) {
        if (this.serverUrlRoot.endsWith(Strings.SLASH)) {
            this.serverUrlRoot = this.serverUrlRoot.substring(0,
                    this.serverUrlRoot.length() - Strings.SLASH.length());
        }
        StringBuffer url = new StringBuffer(this.serverUrlRoot).append("/rpc/invoke/")
                .append(beanId).append("/").append(methodName);
        return url.toString();
    }

    private Map<String, Object> getInvokeParams(Object[] args) throws Exception {
        Map<String, Object> params = new HashMap<>();
        if (args.length > 0) {
            params.put("args", this.serializer.serialize(args));

            // Class<?>[] argTypes = new Class<?>[args.length];
            // for (int i = 0; i < args.length; i++) {
            // argTypes[i] = args[i].getClass();
            // }
            // String typeString =
            // this.serializer.serializeArray(argTypes);
            // params.put("types", typeString);
        }
        return params;
    }

    /**
     * 获取指定URI的响应内容
     *
     * @param request
     *            请求
     * @return 响应内容
     * @throws Exception
     *             如果响应中有错误
     */
    @SuppressWarnings("unchecked")
    private String requestContent(String url, Map<String, Object> params) throws Exception {
        Binate<Integer, String> response = request(url, params);
        int statusCode = response.getLeft();
        String content = response.getRight();
        switch (statusCode) {
        case HttpServletResponse.SC_OK: { // 正常
            return content;
        }
        case HandledError.SC_HANDLED_ERROR: { // 业务异常
            throw new AjaxException(this.serializer.deserialize(content, Map.class));
        }
        default: { // 其他错误
            throw new AjaxException(content);
        }
        }
    }

    @Override
    public <T> T invoke(String beanId, String methodName, Object[] args, Class<T> resultType)
            throws Exception {
        String url = getInvokeUrl(beanId, methodName);
        Map<String, Object> params = getInvokeParams(args);
        String response = requestContent(url, params);
        return this.serializer.deserialize(response, resultType);
    }

    @Override
    public <T> List<T> invoke4List(String beanId, String methodName, Object[] args,
            Class<T> resultElementType) throws Exception {
        String url = getInvokeUrl(beanId, methodName);
        Map<String, Object> params = getInvokeParams(args);
        String response = requestContent(url, params);
        return this.serializer.deserializeList(response, resultElementType);
    }

    private Map<String, Object> getInvokeParams(Map<String, Object> args) throws Exception {
        Map<String, Object> params = new HashMap<>();
        for (Entry<String, Object> entry : args.entrySet()) {
            params.put(entry.getKey(), this.serializer.serialize(entry.getValue()));
        }
        return params;
    }

    @Override
    public <T> T invoke(String beanId, String methodName, Map<String, Object> args,
            Class<T> resultType) throws Exception {
        String url = getInvokeUrl(beanId, methodName);
        Map<String, Object> params = getInvokeParams(args);
        String response = requestContent(url, params);
        return this.serializer.deserialize(response, resultType);
    }

    @Override
    public <T> List<T> invoke4List(String beanId, String methodName, Map<String, Object> args,
            Class<T> resultElementType) throws Exception {
        String url = getInvokeUrl(beanId, methodName);
        Map<String, Object> params = getInvokeParams(args);
        String response = requestContent(url, params);
        return this.serializer.deserializeList(response, resultElementType);
    }
}
