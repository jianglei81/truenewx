package org.truenewx.web.security.login;

/**
 * 带有记住我标志的登录Token，类似Shiro中的RememberMeAuthenticationToken
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface RememberMeLoginToken extends LoginToken {

    boolean isRememberMe();

}
