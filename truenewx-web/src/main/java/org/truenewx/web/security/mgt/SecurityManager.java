package org.truenewx.web.security.mgt;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.web.security.authority.AuthorizationInfo;
import org.truenewx.web.security.login.LoginToken;
import org.truenewx.web.security.subject.Subject;

/**
 * 安全管理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SecurityManager extends SubjectManager {

    void login(Subject subject, LoginToken token) throws HandleableException;

    Object getUser(Subject subject);

    AuthorizationInfo getAuthorizationInfo(Subject subject);

    boolean hasRole(Subject subject, String role);

    void validateRole(Subject subject, String role) throws BusinessException;

    boolean isPermitted(Subject subject, String permission);

    void validatePermission(Subject subject, String permission) throws BusinessException;

    void logout(Subject subject) throws BusinessException;

}
