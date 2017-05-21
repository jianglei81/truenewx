package org.truenewx.web.security.realm;

import javax.servlet.http.Cookie;

/**
 * 支持“记住我”功能的校验领域
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface RememberMeRealm<T> extends Realm<T> {

    /**
     * 根据远程访问地址和Cookie获取登录用户
     * 
     * @param host
     *            远程访问地址
     * @param cookies
     *            当前所有cookie
     * @return 登录用户，如果登录验证不通过则返回null
     */
    T getLoginUser(String host, Cookie[] cookies);

}
