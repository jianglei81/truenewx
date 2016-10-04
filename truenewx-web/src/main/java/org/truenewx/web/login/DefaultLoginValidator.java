package org.truenewx.web.login;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.truenewx.web.util.WebUtil;

/**
 * 默认的登录校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DefaultLoginValidator implements LoginValidator {

    /**
     * 登录判定
     */
    private LoginPredicate loginPredicate;

    /**
     * 登录页面
     */
    private String loginPage;

    public void setLoginPredicate(final LoginPredicate loginPredicate) {
        this.loginPredicate = loginPredicate;
    }

    public void setLoginPage(final String loginPage) {
        this.loginPage = loginPage;
    }

    @Override
    public final String validateLogin(final HttpServletRequest request,
                    final HttpServletResponse response) {
        final boolean logined = this.loginPredicate.isLogined(request, response);
        return getNextPage(request, response, logined);
    }

    protected String getNextPage(final HttpServletRequest request,
                    final HttpServletResponse response, final boolean logined) {
        if (logined) {
            return null;
        }
        if (WebUtil.isAjaxRequest(request)) { // AJAX请求未登录时返回错误状态
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        } else { // 普通请求跳转至登录页面
            return appendOriginalUrl(this.loginPage, request);
        }
    }

    /**
     * 在指定URL上附加指定请求的原始URL
     *
     * @param url
     *            URL
     * @param request
     *            请求
     * @return 附加了原始URL后的URL
     *
     * @author jianglei
     */
    protected final String appendOriginalUrl(final String url, final HttpServletRequest request) {
        final String originalUrl = WebUtil.getRelativeRequestUrlWithQueryString(request, true);
        return MessageFormat.format(url, originalUrl);
    }

}
