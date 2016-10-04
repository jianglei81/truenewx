package org.truenewx.web.authority.validator;

import org.apache.commons.lang3.ArrayUtils;
import org.truenewx.web.authority.exception.AuthorityException;
import org.truenewx.web.authority.exception.DisabledAuthorityException;
import org.truenewx.web.authority.resolver.AuthorityResolver;

/**
 * 禁用权限校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DisabledAuthorityValidator extends DefaultAuthorityValidator {

    public DisabledAuthorityValidator(final AuthorityResolver resolver) {
        super(resolver);
    }

    @Override
    protected void validate(final String validatedAuthority, final String[] userAuthorities)
                    throws AuthorityException {
        if (ArrayUtils.contains(userAuthorities, validatedAuthority)) {
            throw new DisabledAuthorityException(validatedAuthority);
        }
    }

}
