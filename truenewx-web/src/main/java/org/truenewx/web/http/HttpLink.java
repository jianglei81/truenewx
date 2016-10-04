package org.truenewx.web.http;

import java.io.Serializable;

import org.springframework.http.HttpMethod;
import org.truenewx.core.util.StringUtil;

/**
 * HTTP连接
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class HttpLink implements Serializable {

    private static final long serialVersionUID = -787088699797769453L;

    /**
     * 链接地址
     */
    private String href;
    /**
     * 方法类型
     */
    private HttpMethod method;

    /**
     * HTTP连接构造方法
     *
     * @param href
     *            链接地址
     */
    public HttpLink(final String href) {
        this.href = href;
    }

    /**
     * HTTP连接构造方法
     *
     * @param href
     *            链接地址
     * @param method
     *            方法类型
     */
    public HttpLink(final String href, final HttpMethod method) {
        this.href = href;
        this.method = method;
    }

    /**
     * @return 链接地址
     */
    public String getHref() {
        return this.href;
    }

    /**
     * @param href
     *            链接地址
     */
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     * @return 方法类型
     */
    public HttpMethod getMethod() {
        return this.method;
    }

    /**
     * @param method
     *            方法类型
     */
    public void setMethod(final HttpMethod method) {
        this.method = method;
    }

    /**
     * 判断是否匹配指定方法类型
     *
     * @param method
     *            方法类型
     * @return 是否匹配指定方法类型
     */
    public boolean isMatched(final HttpMethod method) {
        return this.method == null || this.method == method;
    }

    /**
     * 判断是否匹配指定链接地址和方法类型
     *
     * @param href
     *            链接地址
     * @param method
     *            方法类型
     * @return 是否匹配指定链接地址和方法类型
     */
    public boolean isMatched(final String href, final HttpMethod method) {
        return this.href != null && StringUtil.antPathMatch(href, this.href) && isMatched(method);
    }
}
