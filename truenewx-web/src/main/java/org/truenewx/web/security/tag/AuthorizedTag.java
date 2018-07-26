package org.truenewx.web.security.tag;

import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.subject.Subject;

/**
 * 判断当前用户是否已获指定权限的标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuthorizedTag extends AuthorizeTagSupport {

    private static final long serialVersionUID = 6818415434978927316L;

    @Override
    protected boolean predicateAuthorize(Subject subject) {
        return subject != null && subject.isAuthorized(new Authority(this.role, this.permission));
    }

}
