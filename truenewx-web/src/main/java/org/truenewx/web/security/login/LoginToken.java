package org.truenewx.web.security.login;

/**
 * 登录Token，类似Shiro中的AuthenticationToken
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface LoginToken {

    /**
     *
     * @return 能唯一表示一个用户的标识，一般为用户名
     */
    Object getPrincipal();

    /**
     * 
     * @return 登录凭证，一般为密码
     */
    Object getCredentials();

}
