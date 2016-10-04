package org.truenewx.web.authority.exception;

import org.truenewx.core.exception.BusinessException;

/**
 * 权限异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AuthorityException extends BusinessException {

    private static final long serialVersionUID = 4643413386935891608L;

    private String authority;

    public AuthorityException(final String code, final String authority) {
        super(code);
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

}
