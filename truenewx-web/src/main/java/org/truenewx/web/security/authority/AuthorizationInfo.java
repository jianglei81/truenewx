package org.truenewx.web.security.authority;

/**
 * 授权信息
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AuthorizationInfo {

    /**
     *
     * @return 用户具有的角色集合
     */
    Iterable<String> getRoles();

    /**
     *
     * @return 用户具有的权限集合
     */
    Iterable<String> getPermissions();

    /**
     *
     * @return 是否需要缓存当前授权信息对象
     */
    boolean isCaching();

}
