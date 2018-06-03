package org.truenewx.web.security.mgt;

import java.util.Enumeration;
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
    public void afterInitialized(ApplicationContext context) throws Exception {
        @SuppressWarnings("rawtypes")
        Map<String, Realm> beans = context.getBeansOfType(Realm.class);
        for (Realm<?> realm : beans.values()) {
            Class<?> userClass = realm.getUserClass();
            // 一个用户类型只能有一个Realm
            Assert.isNull(this.realms.put(userClass, realm),
                    "one userClass can only have one realm");
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> Realm<T> getRealm(Class<T> userClass) {
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
    public Subject getSubject(HttpServletRequest request, HttpServletResponse response) {
        return getSubject(request, response, null);
    }

    @Override
    public Subject getSubject(HttpServletRequest request, HttpServletResponse response,
            Class<?> userClass) {
        Realm<?> realm = getRealm(userClass);
        if (realm != null) { // 用户类型对应的Realm存在才有效
            return new DelegatingSubject(request, response, userClass, this);
        }
        return null;
    }

    @Override
    public Object getUser(Subject subject, boolean auto) {
        Realm<?> realm = getRealm(subject.getUserClass());
        if (realm != null) {
            HttpServletRequest request = subject.getServletRequest();
            HttpSession session = request.getSession(auto);
            if (session != null) {
                Object user = session.getAttribute(realm.getUserSessionName());
                // 如果从会话中无法取得用户，则尝试自动登录验证
                if (user == null && auto && realm instanceof RememberMeRealm) {
                    String host = WebUtil.getRemoteAddrIp(request);
                    Cookie[] cookies = request.getCookies();
                    user = ((RememberMeRealm<?>) realm).getLoginUser(host, cookies);
                    session.setAttribute(realm.getUserSessionName(), user);
                    // cookie可能被修改，重新回写以生效
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            cookie.setPath(request.getContextPath()); // 统一路径以便于删除
                            subject.getServletResponse().addCookie(cookie);
                        }
                    }
                }
                return user;
            }
        }
        return null;
    }

    @Override
    public void login(Subject subject, LoginToken token) throws HandleableException {
        Realm<?> realm = getRealm(subject.getUserClass());
        if (realm != null) {
            LoginInfo loginInfo = realm.getLoginInfo(token);
            if (loginInfo != null) {
                HttpServletRequest request = subject.getServletRequest();
                request.getSession().invalidate(); // 重置会话，以避免新登录用户取得原用户会话信息
                HttpSession session = request.getSession(true); // 重新创建会话对象
                // 登录用户保存至会话
                session.setAttribute(realm.getUserSessionName(), loginInfo.getUser());
                // 保存cookie
                for (Cookie cookie : loginInfo.getCookies()) {
                    cookie.setPath(request.getContextPath()); // 统一路径以便于删除
                    subject.getServletResponse().addCookie(cookie);
                }
            }
        }
    }

    protected String getAuthorizationSessionName(Realm<?> realm) {
        return realm.getUserSessionName() + Strings.UNDERLINE + Authorization.class.getSimpleName();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Authorization getAuthorization(Subject subject, boolean reset) {
        Realm realm = getRealm(subject.getUserClass());
        if (realm != null) {
            HttpSession session = subject.getServletRequest().getSession();
            String authorizationSessionName = getAuthorizationSessionName(realm);
            if (reset) {
                session.removeAttribute(authorizationSessionName);
            }
            AuthorizationInfo ai = (AuthorizationInfo) session
                    .getAttribute(authorizationSessionName);
            if (ai == null) {
                Object user = getUser(subject, false);
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
    public boolean isAuthorized(Subject subject, Authority authority) {
        if (authority != null) {
            Authorization authorization = getAuthorization(subject, false);
            return authority.isContained(authorization);
        }
        return false;
    }

    @Override
    public void validateAuthority(Subject subject, Authority authority) throws BusinessException {
        if (!isAuthorized(subject, authority)) {
            throw new NoAuthorityException(authority);
        }
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void logout(Subject subject) throws BusinessException {
        Realm realm = getRealm(subject.getUserClass());
        if (realm != null) {
            Object user = getUser(subject, false);
            LogoutInfo logoutInfo = realm.getLogoutInfo(user);
            if (logoutInfo != null) {
                HttpServletRequest request = subject.getServletRequest();
                HttpSession session = request.getSession();
                if (logoutInfo.isInvalidatingSession()) {
                    session.invalidate();
                } else { // 不用无效化session则移除session中的属性
                    removeSessionAttributesOnLogout(session);
                }

                // 移除需要移除的cookie
                Iterable<String> cookieNames = logoutInfo.getCookieNames();
                if (cookieNames != null) {
                    HttpServletResponse response = subject.getServletResponse();
                    for (String cookieName : cookieNames) {
                        WebUtil.removeCookie(response, cookieName, request.getContextPath());
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
    protected void removeSessionAttributesOnLogout(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            session.removeAttribute(attributeNames.nextElement());
        }
    }

}
