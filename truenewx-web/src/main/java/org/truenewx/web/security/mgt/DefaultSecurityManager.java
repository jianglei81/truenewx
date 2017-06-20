package org.truenewx.web.security.mgt;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.truenewx.core.Strings;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;
import org.truenewx.web.security.authority.AuthorizationInfo;
import org.truenewx.web.security.login.LoginInfo;
import org.truenewx.web.security.login.LoginToken;
import org.truenewx.web.security.login.LogoutInfo;
import org.truenewx.web.security.realm.Realm;
import org.truenewx.web.security.realm.RememberMeRealm;
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
    public Object getUser(final Subject subject, final boolean auto) {
        final Realm<?> realm = getRealm(subject.getUserClass());
        if (realm != null) {
            final HttpServletRequest request = subject.getServletRequest();
            final HttpSession session = request.getSession(false);
            if (session != null) {
                Object user = session.getAttribute(realm.getUserSessionName());
                // 如果从会话中无法取得用户，则尝试自动登录验证
                if (user == null && auto && realm instanceof RememberMeRealm) {
                    final String host = WebUtil.getRemoteAddrIp(request);
                    final Cookie[] cookies = request.getCookies();
                    user = ((RememberMeRealm<?>) realm).getLoginUser(host, cookies);
                    session.setAttribute(realm.getUserSessionName(), user);
                    // cookie可能被修改，重新回写以生效
                    for (final Cookie cookie : cookies) {
                        subject.getServletResponse().addCookie(cookie);
                    }
                }
                return user;
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
                request.getSession().invalidate(); // 重置会话，以避免新登录用户取得原用户会话信息
                final HttpSession session = request.getSession(true); // 重新创建会话对象
                // 登录用户保存至会话
                session.setAttribute(realm.getUserSessionName(), loginInfo.getUser());
                // 保存cookie
                for (final Cookie cookie : loginInfo.getCookies()) {
                    // cookie路径加上工程根目录
                    final String contextPath = request.getContextPath();
                    if (StringUtils.isNotEmpty(contextPath)) {
                        cookie.setPath(contextPath + cookie.getPath());
                    }
                    subject.getServletResponse().addCookie(cookie);
                }
            }
        }
    }

    protected String getAuthorizationSessionName(final Realm<?> realm) {
        return realm.getUserSessionName() + Strings.UNDERLINE + Authorization.class.getSimpleName();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Authorization getAuthorization(final Subject subject) {
        final Realm realm = getRealm(subject.getUserClass());
        if (realm != null) {
            final HttpSession session = subject.getServletRequest().getSession();
            final String authorizationSessionName = getAuthorizationSessionName(realm);
            AuthorizationInfo ai = (AuthorizationInfo) session
                    .getAttribute(authorizationSessionName);
            if (ai == null) {
                final Object user = getUser(subject, false);
                if (user != null) {
                    ai = realm.getAuthorizationInfo(user);
                    if (ai != null && ai.isCaching()) {
                        session.setAttribute(authorizationSessionName, ai);
                    }
                }
            }
            return ai;
        }
        return null;
    }

    @Override
    public boolean isAuthorized(final Subject subject, final Authority authority) {
        final Authorization authorization = getAuthorization(subject);
        return authority == null || authority.isContained(authorization);
    }

    @Override
    public void validateAuthority(final Subject subject, final Authority authority)
            throws BusinessException {
        if (!isAuthorized(subject, authority)) {
            throw new NoAuthorityException(authority);
        }
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void logout(final Subject subject) throws BusinessException {
        final Realm realm = getRealm(subject.getUserClass());
        if (realm != null) {
            final Object user = getUser(subject, false);
            final LogoutInfo logoutInfo = realm.getLogoutInfo(user);
            if (logoutInfo != null) {
                final HttpServletRequest request = subject.getServletRequest();
                final HttpSession session = request.getSession();
                if (logoutInfo.isInvalidatingSession()) {
                    session.invalidate();
                } else { // 不用无效化session则移除session中的属性
                    removeSessionAttributesOnLogout(session);
                }

                // 移除需要移除的cookie
                final Iterable<String> cookieNames = logoutInfo.getCookieNames();
                if (cookieNames != null) {
                    final HttpServletResponse response = subject.getServletResponse();
                    for (final String cookieName : cookieNames) {
                        final Cookie cookie = WebUtil.getCookie(request, cookieName);
                        if (cookie != null) {
                            cookie.setMaxAge(0);
                            cookie.setValue(Strings.EMPTY);
                            response.addCookie(cookie);
                        }
                    }
                }
                realm.onLogouted(user);
            }
        }
    }

    /**
     * 用户登出时移除会话中的属性，默认移除所有属性，子类可覆写进行选择性的移除
     *
     * @param session
     *            会话
     */
    protected void removeSessionAttributesOnLogout(final HttpSession session) {
        final Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            session.removeAttribute(attributeNames.nextElement());
        }
    }

}
