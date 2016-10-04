package org.truenewx.web.authority.resolver;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AuthorityResolver {
    /**
     * 获取当前权限清单
     *
     * @param request
     *            HTTP请求
     * @return 当前权限清单
     */
    String[] getAuthorities(HttpServletRequest request);

}
