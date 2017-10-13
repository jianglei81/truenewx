package org.truenewx.web.security.subject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;
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

    <T extends Authorization> T getAuthorization(boolean reset);

    boolean isAuthorized(Authority authority);

    void validateAuthority(Authority authority) throws BusinessException;

    void logout() throws BusinessException;

}
