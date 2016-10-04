package org.truenewx.web.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.web.util.WebUtil;

/**
 * 尝试自动登录的登录判定
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AutoLoginPredicate implements LoginPredicate {

    @Autowired
    private AutoLoginer autoLoginer;

    @Override
    public boolean isLogined(final HttpServletRequest request, final HttpServletResponse response) {
        if (this.autoLoginer.isLogined(request, response)) {
            return true;
        }
        // 尝试自动登录
        final Cookie loginNameCookie = WebUtil.getCookie(request,
                        this.autoLoginer.getLoginNameCookieName());
        if (loginNameCookie != null) {
            final String loginName = WebUtil.decodeParameter(request, loginNameCookie.getValue());
            if (StringUtils.isNotEmpty(loginName)) {
                final String passwordCookieName = this.autoLoginer.getPasswordCookieName();
                final String password = WebUtil.getCookieValue(request, passwordCookieName);
                try {
                    this.autoLoginer.login(loginName, password, request, response);
                    return true;
                } catch (final HandleableException e) {
                    // 正常情况不会出现登录异常，除非密码被修改，此时清除cookie
                    WebUtil.removeCookie(request, response, loginNameCookie.getName());
                    WebUtil.removeCookie(request, response, passwordCookieName);
                }
            }
        }
        return false;
    }

}
