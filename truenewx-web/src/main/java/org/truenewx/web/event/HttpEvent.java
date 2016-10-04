package org.truenewx.web.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.truenewx.core.event.Event;

/**
 * HTTP事件
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class HttpEvent implements Event {
    /**
     * HTTP请求
     */
    private HttpServletRequest request;
    /**
     * HTTP响应
     */
    private HttpServletResponse response;

    public HttpEvent(final HttpServletRequest request, final HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * @return HTTP请求
     */
    public final HttpServletRequest getRequest() {
        return this.request;
    }

    /**
     * @return HTTP响应
     */
    public final HttpServletResponse getResponse() {
        return this.response;
    }

}
