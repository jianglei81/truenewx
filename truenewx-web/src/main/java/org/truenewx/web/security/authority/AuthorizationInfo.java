package org.truenewx.web.security.authority;

/**
 * 授权信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AuthorizationInfo extends Authorization {

    /**
     *
     * @return 是否需要缓存当前授权信息对象
     */
    boolean isCaching();

}
