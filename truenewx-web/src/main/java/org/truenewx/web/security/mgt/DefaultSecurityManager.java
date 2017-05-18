package org.truenewx.web.security.mgt;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.truenewx.core.Strings;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.web.security.authority.AuthorizationInfo;
import org.truenewx.web.security.login.LoginInfo;
import org.truenewx.web.security.login.LoginToken;
import org.truenewx.web.security.login.LogoutInfo;
import org.truenewx.web.security.realm.Realm;
import org.truenewx.web.security.subject.DelegatingSubject;
import org.truenewx.web.security.subject.Subject;
import org.truenewx.web.util.WebUtil;

/**
 * 默认的安全管理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DefaultSecurityManager implements SecurityManager, ContextInitializedBean {

    private Map<Class<?>, Realm<?>> realms = new HashMap<>();

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        @SuppressWarnings("rawtypes")
        final Map<String, Realm> beans = context.getBeansOfType(Realm.class);
        for (final Realm<?> realm : beans.values()) {
            final Class<?> userClass = realm.getUserClass();
            // 一个用户类型只能有一个Realm
            Assert.isNull(this.realms.put(userClass, realm));
        }
    }

    @SuppressWarnings("unchecked")
    protected final <T> Realm<T> getRealm(final Class<T> userClass) {
        if (this.realms.size() > 0) {
            if (userClass == null) {
                if (this.realms.size() > 1) {
                    throw new NonUniqueRealmException();
                }
                return (Realm<T>) this.realms.values().iterator().next();
            } else {
                return (Realm<T>) this.realms.get(userClass);
            }
        }
        return null;
    }

    @Override
    public Subject getSubject(final HttpServletRequest request,
            final HttpServletResponse response) {
        return getSubject(request, response, null);
    }

    @Override
    public Subject getSubject(final HttpServletRequest request, final HttpServletResponse response,
            final Class<?> userClass) {
        final Realm<?> realm = getRealm(userClass);
        if (realm != null) { // 用户类型对应的Realm存在才有效
            return new DelegatingSubject(request, response, userClass, this);
        }
        return null;
    }

    @Override
    public Object getUser(final Subject subject) {
        final Realm<?> realm = getRealm(subject.getUserClass());
        if (realm != null) {
            final HttpSession session = subject.getServletRequest().getSession(false);
            if (session != null) {
                return session.getAttribute(realm.getUserSessionName());
            }
        }
        return null;
    }

    @Override
    public void login(final Subject subject, final LoginToken token) throws HandleableException {
        final Realm<?> realm = getRealm(subject.getUserClass());
        if (realm != null) {
            final LoginInfo loginInfo = realm.getLoginInfo(token);
            if (loginInfo != null) {
                final HttpServletRequest request = subject.getServletRequest();
                final HttpSession session = request.getSession();
                // 登录用户保存至会话
                session.setAttribute(realm.getUserSessionName(), loginInfo.getUser());
                // 保存cookie
                for (final Cookie cookie : loginInfo.getCookies()) {
                    cookie.setPath(request.getContextPath()); // 所有cookie的路径都设置为工程根目录
                    subject.getServletResponse().addCookie(cookie);
                }
            }
        }
    }

    protected String getAuthorizationInfoSessionName(final Realm<?> realm) {
        return realm.getUserSessionName() + Strings.UNDERLINE + "Authorization";
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AuthorizationInfo getAuthorizationInfo(final Subject subject) {
        final Realm realm = getRealm(subject.getUserClass());
        if (realm != null) {
            final HttpSession session = subject.getServletRequest().getSession();
            final String authorizationInfoSessionName = getAuthorizationInfoSessionName(realm);
            AuthorizationInfo ai = (AuthorizationInfo) session
                    .getAttribute(authorizationInfoSessionName);
            if (ai == null) {
                final Object user = getUser(subject);
                ai = realm.getAuthorizationInfo(user);
                if (ai != null && ai.isCaching()) {
                    session.setAttribute(authorizationInfoSessionName, ai);
                }
            }
            return ai;
        }
        return null;
    }

    @Override
    public boolean hasRole(final Subject subject, final String role) {
        final AuthorizationInfo ai = getAuthorizationInfo(subject);
        if (ai != null) {
            for (final String r : ai.getRoles()) {
                // 星号*表示具有所有角色
                if (Strings.ASTERISK.equals(r) || r.equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void validateRole(final Subject subject, final String role) throws BusinessException {
        if (role != null && !hasRole(subject, role)) {
            throw new BusinessException(SecurityExceptionCodes.NO_ROLE, role);
        }
    }

    @Override
    public boolean isPermitted(final Subject subject, final String permission) {
        final AuthorizationInfo ai = getAuthorizationInfo(subject);
        if (ai != null) {
            for (final String p : ai.getPermissions()) {
                // 星号*表示具有所有权限
                if (Strings.ASTERISK.equals(p) || p.equals(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void validatePermission(final Subject subject, final String permission)
            throws BusinessException {
        if (permission != null && !isPermitted(subject, permission)) {
            throw new BusinessException(SecurityExceptionCodes.NO_PERMISSION, permission);
        }
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void logout(final Subject subject) throws BusinessException {
        final Realm realm = getRealm(subject.getUserClass());
        if (realm != null) {
            final Object user = getUser(subject);
            final LogoutInfo logoutInfo = realm.getLogoutInfo(user);
            if (logoutInfo != null) {
                final HttpServletRequest request = subject.getServletRequest();
                final HttpSession session = request.getSession();
                if (logoutInfo.isInvalidatingSession()) {
                    session.invalidate();
                } else { // 不用无效化session则逐一移除session中需要移除的属性
                    // 移除会话中的用户
                    session.removeAttribute(realm.getUserSessionName());
                    // 移除会话中可能缓存的授权信息对象
                    session.removeAttribute(getAuthorizationInfoSessionName(realm));
                }

                // 移除需要移除的cookie
                final Iterable<String> cookieNames = logoutInfo.getCookieNames();
                if (cookieNames != null) {
                    final HttpServletResponse response = subject.getServletResponse();
                    for (final String cookieName : cookieNames) {
                        WebUtil.removeCookie(request, response, cookieName);
                    }
                }
            }
        }
    }

}
