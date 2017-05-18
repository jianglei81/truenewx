package org.truenewx.web.security.subject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.web.security.authority.AuthorizationInfo;
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
        return (T) this.securityManager.getUser(this);
    }

    @Override
    public boolean isLogined() {
        return getUser() != null;
    }

    @Override
    public void login(final LoginToken token) throws HandleableException {
        this.securityManager.login(this, token);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends AuthorizationInfo> T getAuthorizationInfo() {
        return (T) this.securityManager.getAuthorizationInfo(this);
    }

    @Override
    public boolean hasRole(final String role) {
        return this.securityManager.hasRole(this, role);
    }

    @Override
    public void validateRole(final String role) throws BusinessException {
        this.securityManager.validateRole(this, role);
    }

    @Override
    public boolean isPermitted(final String permission) {
        return this.securityManager.isPermitted(this, permission);
    }

    @Override
    public void validatePermission(final String permission) throws BusinessException {
        this.securityManager.validatePermission(this, permission);
    }

    @Override
    public void logout() throws BusinessException {
        this.securityManager.logout(this);
    }

}
