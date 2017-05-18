package org.truenewx.web.security.subject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.web.security.authority.AuthorizationInfo;
import org.truenewx.web.security.login.LoginToken;

/**
 * 类似Shiro中的Subject，用于表示一个用户的相关信息
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            用户类型
 */
public interface Subject {

    HttpServletRequest getServletRequest();

    HttpServletResponse getServletResponse();

    Class<?> getUserClass();

    <T> T getUser();

    boolean isLogined();

    void login(LoginToken token) throws HandleableException;

    <T extends AuthorizationInfo> T getAuthorizationInfo();

    boolean hasRole(String role);

    void validateRole(String role) throws BusinessException;

    boolean isPermitted(String permission);

    void validatePermission(String permission) throws BusinessException;

    void logout() throws BusinessException;

}
