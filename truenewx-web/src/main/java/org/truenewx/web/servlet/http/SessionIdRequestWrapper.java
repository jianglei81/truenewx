package org.truenewx.web.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.truenewx.web.util.WebUtil;

/**
 * 处理sessionId的请求包装器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SessionIdRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletResponse response;

    public SessionIdRequestWrapper(final HttpServletRequest request,
                    final HttpServletResponse response) {
        super(request);
        this.response = response;
    }

    @Override
    public HttpSession getSession() {
        final HttpSession session = super.getSession();
        processSession(session);
        return session;
    }

    @Override
    public HttpSession getSession(final boolean create) {
        final HttpSession session = super.getSession(create);
        processSession(session);
        return session;
    }

    private void processSession(final HttpSession session) {
        if (this.response == null || session == null) {
            return;
        }

        // cookieOverWritten - 用于过滤多个Set-Cookie头的标志
        final Object cookieOverWritten = getAttribute("COOKIE_OVERWRITTEN_FLAG");
        if (cookieOverWritten == null && isSecure() && session.isNew()) {
            // 当是https协议，且新session时，创建JSESSIONID cookie以欺骗浏览器
            // 有效时间为浏览器打开或超时
            WebUtil.addCookie(this, this.response, "JSESSIONID", session.getId(), -1);

            setAttribute("COOKIE_OVERWRITTEN_FLAG", "true"); // 过滤多个Set-Cookie头的标志
        }
    }
}
