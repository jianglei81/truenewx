package org.truenewx.core.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.truenewx.core.Strings;
import org.truenewx.core.tuple.Binary;
import org.truenewx.core.tuple.Binate;

/**
 * HTTP客户端请求支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ClientRequestSupport {
    private CloseableHttpClient client = HttpClientBuilder.create().build();
    private String httpMethod = "POST";
    private String encoding = Strings.DEFAULT_ENCODING;
    private int timeout;

    public void setClient(final CloseableHttpClient client) {
        this.client = client;
    }

    public void setMethod(final String method) {
        this.httpMethod = method;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    /**
     * 获取指定URI的响应结果
     *
     * @param request
     *            请求
     * @return 响应状态码-响应体内容
     * @throws Exception
     *             如果请求过程中有错误
     */
    public Binate<Integer, String> request(final String url, final Map<String, Object> params)
                    throws Exception {
        return request(url, params, this.encoding);
    }

    /**
     * 获取指定URI的响应结果
     *
     * @param request
     *            请求
     * @return 响应状态码-响应体内容
     * @throws Exception
     *             如果请求过程中有错误
     */
    public Binate<Integer, String> request(final String url, final Map<String, Object> params,
                    final String encoding) throws Exception {
        final HttpRequestBase request;
        switch (this.httpMethod.toUpperCase()) {
        case "GET":
            request = new HttpGet(NetUtil.mergeParams(url, params, null));
            break;
        case "POST":
            final HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(toNameValuePairs(params), encoding));
            request = post;
            break;
        default:
            request = null;
        }
        if (request != null) {
            if (this.timeout > 0) {
                final RequestConfig requestConfig = RequestConfig.custom()
                                .setConnectionRequestTimeout(this.timeout)
                                .setConnectTimeout(this.timeout).setSocketTimeout(this.timeout)
                                .build();
                request.setConfig(requestConfig);
            }
            final CloseableHttpResponse response = this.client.execute(request);
            if (response != null) {
                try {
                    final int statusCode = response.getStatusLine().getStatusCode();
                    final String content = EntityUtils.toString(response.getEntity(), encoding);
                    if (statusCode != HttpStatus.SC_OK) {
                        LoggerFactory.getLogger(getClass()).error(content);
                    }
                    return new Binary<Integer, String>(statusCode, content);
                } finally {
                    // 确保关闭请求连接
                    response.close();
                }
            }
        }
        return null;
    }

    private List<NameValuePair> toNameValuePairs(final Map<String, Object> params) {
        final List<NameValuePair> pairs = new ArrayList<>();
        for (final Entry<String, Object> entry : params.entrySet()) {
            final Object value = entry.getValue();
            if (value instanceof Iterable) {
                for (final Object element : (Iterable<?>) value) {
                    if (element != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), element.toString()));
                    }
                }
            } else if (value.getClass().isArray()) {
                final int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    final Object element = Array.get(value, i);
                    if (element != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), element.toString()));
                    }
                }
            } else {
                pairs.add(new BasicNameValuePair(entry.getKey(), value.toString()));
            }
        }
        return pairs;
    }
}
