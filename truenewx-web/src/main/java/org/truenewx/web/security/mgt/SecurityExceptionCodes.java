package org.truenewx.web.security.mgt;

/**
 * 安全异常代码集
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SecurityExceptionCodes {

    private SecurityExceptionCodes() {
    }

    /**
     * 没有指定角色
     */
    public static final String NO_ROLE = "error.security.no_role";

    /**
     * 没有指定权限
     */
    public static final String NO_PERMISSION = "error.security.no_permission";

}
