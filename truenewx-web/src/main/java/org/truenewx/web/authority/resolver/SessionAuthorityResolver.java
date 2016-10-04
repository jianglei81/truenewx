package org.truenewx.web.authority.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 从会话中取权限的权限解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SessionAuthorityResolver implements AuthorityResolver {
    /**
     * 默认的权限会话属性名
     */
    public static final String DEFAULT_AUTHORITY_SESSION_NAME = "_SESSION_AUTHORITIES";

    /**
     * 权限会话属性名
     */
    private String authoritySessionName = DEFAULT_AUTHORITY_SESSION_NAME;

    public void setAuthoritySessionName(final String authoritySessionName) {
        this.authoritySessionName = authoritySessionName;
    }

    @Override
    public String[] getAuthorities(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            return (String[]) session.getAttribute(this.authoritySessionName);
        }
        return null;
    }

}
