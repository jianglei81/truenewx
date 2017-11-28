package org.truenewx.web.security.interceptor;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.util.NetUtil;
import org.truenewx.web.menu.MenuResolver;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.security.annotation.Accessibility;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.mgt.SubjectManager;
import org.truenewx.web.security.subject.Subject;
import org.truenewx.web.util.UrlPatternMatchSupport;
import org.truenewx.web.util.WebUtil;

/**
 * 安全校验拦截器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SecurityValidateInterceptor extends UrlPatternMatchSupport
        implements HandlerInterceptor {
    /**
     * 请求转发的前缀
     */
    public static final String FORWARD_PREFIX = "forward:";

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SubjectManager subjectManager;

    private Class<?> userClass;

    private String loginUrl;

    private Menu menu;

    @Autowired
    public void setSubjectManager(final SubjectManager subjectManager) {
        this.subjectManager = subjectManager;
    }

    /**
     *
     * @param userClass
     *            要拦截的用户类型，如果整个系统只有一种用户类型，则可以不设置
     */
    public void setUserClass(final Class<?> userClass) {
        this.userClass = userClass;
    }

    /**
     *
     * @param loginUrl
     *            未登录时试图访问需登录才能访问的资源时，跳转至的登录页面URL，可通过{0}附带上原访问链接
     */
    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setMenuResolver(final MenuResolver menuResolver) {
        this.menu = menuResolver.getFullMenu();
    }

    private Class<?> getUserClass(final HttpServletRequest request,
            final HttpServletResponse response) {
        if (this.userClass == null) {
            final Subject subject = this.subjectManager.getSubject(request, response);
            if (subject != null) {
                this.userClass = subject.getUserClass();
            }
        }
        return this.userClass;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) throws Exception {
        // 请求为include请求则忽略当前拦截器
        if (WebUtils.isIncludeRequest(request)) {
            return true;
        }
        final String url = WebUtil.getRelativeRequestUrl(request);
        if (matches(url)) { // URL匹配才进行校验
            final Class<?> userClass = getUserClass(request, response);
            final Subject subject = this.subjectManager.getSubject(request, response, userClass);
            if (subject != null && handler instanceof HandlerMethod) { // 能取得subject才进行校验
                final Accessibility accessibility = ((HandlerMethod) handler)
                        .getMethodAnnotation(Accessibility.class);
                // 局域网访问限制校验
                if (!validateLan(url, accessibility, request, response)) {
                    return false;
                }
                final HttpMethod method = HttpMethod.valueOf(request.getMethod());
                // 登录校验
                if (!validateLogin(url, method, accessibility, subject, request, response)) {
                    return false;
                }
                // 授权校验
                if (!validateAuthority(url, method, subject, request)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean validateLan(final String url, final Accessibility accessibility,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final String ip = WebUtil.getRemoteAddrIp(request);
        if (accessibility != null && accessibility.lan() && !NetUtil.isLanIp(ip)) {
            this.logger.warn("Forbidden rpc request {} from {}", url, ip);
            response.sendError(HttpStatus.FORBIDDEN.value()); // 禁止非局域网访问
            return false;
        }
        return true;
    }

    protected boolean validateLogin(final String url, final HttpMethod method,
            final Accessibility accessibility, final Subject subject,
            final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        if (!subject.isLogined()) {
            // 在访问性注解中设置了可匿名访问，则验证通过
            if (accessibility != null && accessibility.anonymous()) {
                return true;
            }
            // 配置菜单中当前链接允许匿名访问，则跳过不作限制
            if (this.menu != null && this.menu.isAnonymous(url, method)) {
                return true;
            }
            // 未登录且不允许匿名访问
            if (WebUtil.isAjaxRequest(request)) { // AJAX请求未登录时，返回错误状态
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            } else { // 普通请求未登录时，跳转至登录页面
                final String originalUrl = WebUtil.getRelativeRequestUrlWithQueryString(request,
                        true);
                String loginUrl = MessageFormat.format(this.loginUrl, originalUrl);
                if (loginUrl.startsWith(FORWARD_PREFIX)) { // 请求转发
                    loginUrl = loginUrl.substring(FORWARD_PREFIX.length());
                    WebUtil.forward(request, response, loginUrl);
                } else { // 直接重定向
                    WebUtil.redirect(request, response, loginUrl);
                }
            }
            return false;
        }
        return true;
    }

    protected boolean validateAuthority(final String url, final HttpMethod method,
            final Subject subject, final HttpServletRequest request) throws BusinessException {
        if (this.menu != null) {
            final Authority authority = this.menu.getAuthority(url, method);
            // 此时授权可能为null，为null时将被视为无访问权限，意味着在配置有菜单的系统中，URL访问均应在菜单配置中进行配置
            subject.validateAuthority(authority);
        }
        return true;
    }

    @Override
    protected boolean matches(final String url) {
        // 始终排除RPC访问
        return !url.startsWith("/rpc/") && super.matches(url);
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(final HttpServletRequest request,
            final HttpServletResponse response, final Object handler, final Exception ex) {
    }

}
