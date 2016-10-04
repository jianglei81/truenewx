package org.truenewx.web.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

/**
 * 处理sessionId的响应包装器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SessionIdResponseWrapper extends HttpServletResponseWrapper {

    public SessionIdResponseWrapper(final HttpServletRequest request,
            final HttpServletResponse response) {
        super(response);
        if (request.isRequestedSessionIdFromURL()) {
            final HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
    }

    @Override
    public String encodeRedirectUrl(final String url) {
        return url;
    }

    @Override
    public String encodeRedirectURL(final String url) {
        return url;
    }

    @Override
    public String encodeUrl(final String url) {
        return url;
    }

    @Override
    public String encodeURL(final String url) {
        return url;
    }

}
