package org.truenewx.web.authority.validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限校验器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AuthorityValidator {

    void validate(HttpServletRequest request, HttpServletResponse response, Object handler,
                    String validatedAuthority) throws Exception;

}
