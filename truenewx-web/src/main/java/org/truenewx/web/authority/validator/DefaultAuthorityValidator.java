package org.truenewx.web.authority.validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.web.authority.exception.AuthorityException;
import org.truenewx.web.authority.exception.NoAuthorityException;
import org.truenewx.web.authority.resolver.AuthorityResolver;

/**
 * 默认权限校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class DefaultAuthorityValidator implements AuthorityValidator {

    private AuthorityResolver resolver;

    public DefaultAuthorityValidator(final AuthorityResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void validate(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final String validatedAuthority) throws Exception {
        if (StringUtils.isNotBlank(validatedAuthority)) {
            final String[] userAuthorities = this.resolver.getAuthorities(request);
            validate(validatedAuthority, userAuthorities);
        }
    }

    protected void validate(final String validatedAuthority, final String[] userAuthorities)
            throws AuthorityException {
        if (userAuthorities == null || !ArrayUtils.contains(userAuthorities, validatedAuthority)) {
            throw new NoAuthorityException(validatedAuthority);
        }
    }

}
