package org.truenewx.web.security.login;

/**
 * 带有请求来源地址的登录Token，类似Shiro中的HostAuthenticationToken
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface HostLoginToken extends LoginToken {

    /**
     *
     * @return 访问者远程地址
     */
    String getHost();

}
