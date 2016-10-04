package org.truenewx.web.authority.exception;

/**
 * 禁用权限异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DisabledAuthorityException extends AuthorityException {

    private static final long serialVersionUID = -7110275980100861214L;

    public DisabledAuthorityException(final String authority) {
        super(AuthorityExceptionCodes.DISABLED_AUTHORITY, authority);
    }

}
