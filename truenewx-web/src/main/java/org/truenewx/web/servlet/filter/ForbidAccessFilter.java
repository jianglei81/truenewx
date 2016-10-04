package org.truenewx.web.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.util.NetUtil;
import org.truenewx.core.util.StringUtil;
import org.truenewx.web.util.WebUtil;

/**
 * 禁止访问过滤器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class ForbidAccessFilter implements Filter {
    private String errorPage;
    private String[] ignoredPatterns = new String[0];
    private boolean valid4Lan = true;

    @Override
    public void init(final FilterConfig config) throws ServletException {
        this.errorPage = config.getInitParameter("errorPage");
        final String ignoredPattern = config.getInitParameter("ignoredPattern");
        if (StringUtils.isNotEmpty(ignoredPattern)) {
            this.ignoredPatterns = ignoredPattern.split(Strings.COMMA);
        }
        final String valid4Lan = config.getInitParameter("valid4Lan");
        if (valid4Lan != null) {
            this.valid4Lan = Boolean.valueOf(valid4Lan);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                    final FilterChain chain) throws IOException, ServletException {
        final String ip = WebUtil.getRemoteAddrIp((HttpServletRequest) request);
        if (!this.valid4Lan && NetUtil.isLanIp(ip)) { // 对局域网无效且请求为局域网请求，则忽略
            chain.doFilter(request, response);
        } else {
            final String url = WebUtil.getRelativeRequestUrl((HttpServletRequest) request);
            if (!Strings.SLASH.equals(url)
                            && !StringUtil.wildcardMatchOneOf(url, this.ignoredPatterns)) {
                if (this.errorPage == null) { // 没设置错误页面则直接返回403错误
                    ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
                } else {
                    request.getRequestDispatcher(this.errorPage).forward(request, response);
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }
}
