package org.truenewx.web.security.mgt;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;
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

    Object getUser(Subject subject, boolean auto);

    Authorization getAuthorization(Subject subject, boolean reset);

    boolean isAuthorized(Subject subject, Authority authority);

    void validateAuthority(Subject subject, Authority authority) throws BusinessException;

    void logout(Subject subject) throws BusinessException;

}
