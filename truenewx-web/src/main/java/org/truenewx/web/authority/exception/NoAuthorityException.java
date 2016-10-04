package org.truenewx.web.authority.exception;

/**
 * 没有权限的异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NoAuthorityException extends AuthorityException {

    private static final long serialVersionUID = -45565972571324618L;

    public NoAuthorityException(final String authority) {
        super(AuthorityExceptionCodes.NO_AUTHORITY, authority);
    }

}
