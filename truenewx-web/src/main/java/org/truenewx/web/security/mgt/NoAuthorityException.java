package org.truenewx.web.security.mgt;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.web.security.authority.Authority;

/**
 * 没有授权异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NoAuthorityException extends BusinessException {

    public static final String CODE = "error.security.no_authority";

    private static final long serialVersionUID = 5633008099906840383L;

    public NoAuthorityException(final Authority authority) {
        super(CODE);
    }

}
