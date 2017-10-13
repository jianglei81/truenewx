package org.truenewx.web.security.subject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;
import org.truenewx.web.security.login.LoginToken;
import org.truenewx.web.security.mgt.SecurityManager;

/**
 * 委派方式实现的Subject，类似Shiro中的DelegatingSubject
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DelegatingSubject implements Subject {

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Class<?> userClass;

    private SecurityManager securityManager;

    public DelegatingSubject(final HttpServletRequest request, final HttpServletResponse response,
            final Class<?> userClass, final SecurityManager securityManager) {
        this.request = request;
        this.response = response;
        this.userClass = userClass;
        this.securityManager = securityManager;
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return this.request;
    }

    @Override
    public HttpServletResponse getServletResponse() {
        return this.response;
    }

    @Override
    public Class<?> getUserClass() {
        return this.userClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getUser() {
        return (T) this.securityManager.getUser(this, false);
    }

    @Override
    public boolean isLogined() {
        // 仅在判断是否已登录时尝试自动登录，因为自动登录在一次请求中应该只被调用一次，而本方法只在请求拦截时被调用
        return this.securityManager.getUser(this, true) != null;
    }

    @Override
    public void login(final LoginToken token) throws HandleableException {
        this.securityManager.login(this, token);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Authorization> T getAuthorization(final boolean reset) {
        return (T) this.securityManager.getAuthorization(this, reset);
    }

    @Override
    public boolean isAuthorized(final Authority authority) {
        return this.securityManager.isAuthorized(this, authority);
    }

    @Override
    public void validateAuthority(final Authority authority) throws BusinessException {
        this.securityManager.validateAuthority(this, authority);
    }

    @Override
    public void logout() throws BusinessException {
        this.securityManager.logout(this);
    }

}
