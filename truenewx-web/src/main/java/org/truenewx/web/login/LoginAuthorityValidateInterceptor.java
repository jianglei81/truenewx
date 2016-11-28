package org.truenewx.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.truenewx.core.util.StringUtil;
import org.truenewx.web.authority.validator.AuthorityValidator;
import org.truenewx.web.menu.MenuResolver;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.util.WebUtil;

/**
 * 登录权限验证拦截器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class LoginAuthorityValidateInterceptor implements HandlerInterceptor {

    /**
     * 请求转发的前缀
     */
    private static final String FORWARD_PREFIX = "forward:";

    /**
     * 登录页面解决器
     */
    private LoginValidator loginValidator;

    /**
     * 不可匿名访问的URL模板集
     */
    private String[] includeUrlPatterns;

    /**
     * 可匿名访问的URL模板集
     */
    private String[] excludeUrlPatterns;

    private Menu menu;

    private AuthorityValidator authorityValidator;

    /**
     * 登录校验器
     */
    public void setLoginValidator(final LoginValidator loginValidator) {
        this.loginValidator = loginValidator;
    }

    /**
     * 不可匿名访问的URL模板集
     */
    public void setIncludeUrlPatterns(final String[] includeUrlPatterns) {
        this.includeUrlPatterns = includeUrlPatterns;
    }

    /**
     * 可匿名访问的URL模板集
     */
    public void setExcludeUrlPatterns(final String[] excludeUrlPatterns) {
        this.excludeUrlPatterns = excludeUrlPatterns;
    }

    public void setMenuResolver(final MenuResolver menuResolver) {
        this.menu = menuResolver.getFullMenu();
    }

    public void setAuthorityValidator(final AuthorityValidator authorityValidator) {
        this.authorityValidator = authorityValidator;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) throws Exception {
        // 没有登录校验器，或请求为include请求则忽略当前拦截器
        if (this.loginValidator == null || WebUtils.isIncludeRequest(request)) {
            return true;
        }
        final String url = WebUtil.getRelativeRequestUrl(request);
        if (isValidatableUrl(url)) {
            String loginPage = this.loginValidator.validateLogin(request, response);
            if (loginPage != null) { // 登录校验失败
                if (loginPage.startsWith(FORWARD_PREFIX)) {
                    loginPage = loginPage.substring(FORWARD_PREFIX.length());
                    WebUtil.forward(request, response, loginPage);
                } else {
                    WebUtil.redirect(request, response, loginPage);
                }
                return false; // 阻止后续拦截器
            }
            // 登录校验成功后，校验权限
            if (this.menu != null && this.authorityValidator != null) {
                final HttpMethod method = HttpMethod.valueOf(request.getMethod());
                final String validatedAuthority = this.menu.getAuth(url, method);
                this.authorityValidator.validate(request, response, handler, validatedAuthority);
            }
        }
        return true;
    }

    private boolean isValidatableUrl(final String url) {
        if (this.includeUrlPatterns == null && this.excludeUrlPatterns == null) { // 所有url都作验证
            return true;
        } else if (this.includeUrlPatterns == null && this.excludeUrlPatterns != null) { // 只验证除exclude以外的url
            if (!StringUtil.wildcardMatchOneOf(url, this.excludeUrlPatterns)) {
                return true;
            }
        } else if (this.includeUrlPatterns != null && this.excludeUrlPatterns == null) { // 只验证include内的url
            if (StringUtil.wildcardMatchOneOf(url, this.includeUrlPatterns)) {
                return true;
            }
        } else if (this.includeUrlPatterns != null && this.excludeUrlPatterns != null) { // 验证include内及exclude以外的url
            if (StringUtil.wildcardMatchOneOf(url, this.includeUrlPatterns)
                    && !StringUtil.wildcardMatchOneOf(url, this.excludeUrlPatterns)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(final HttpServletRequest request,
            final HttpServletResponse response, final Object handler, final Exception ex)
            throws Exception {
    }
}
