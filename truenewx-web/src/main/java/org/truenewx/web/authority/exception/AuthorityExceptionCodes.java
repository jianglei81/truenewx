package org.truenewx.web.authority.exception;

/**
 * 权限异常错误码集
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuthorityExceptionCodes {

    private AuthorityExceptionCodes() {
    }

    /**
     * 没有指定权限
     */
    public static final String NO_AUTHORITY = "error.authority.no_authority";

    /**
     * 指定权限被禁用
     */
    public static final String DISABLED_AUTHORITY = "error.authority.disabled_authority";

}
