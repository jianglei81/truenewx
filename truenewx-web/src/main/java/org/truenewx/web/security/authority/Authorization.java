package org.truenewx.web.security.authority;

/**
 * 授权集=角色集+权限集
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface Authorization {

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

}
