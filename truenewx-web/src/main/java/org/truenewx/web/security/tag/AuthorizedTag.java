package org.truenewx.web.security.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.context.ApplicationContext;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.mgt.SecurityManager;
import org.truenewx.web.security.mgt.SubjectManager;
import org.truenewx.web.security.subject.Subject;
import org.truenewx.web.spring.util.SpringWebUtil;

/**
 * 判断当前用户是否已获指定权限的标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class AuthorizedTag extends TagSupport {

    private static final long serialVersionUID = 6818415434978927316L;

    private Class<?> userClass;
    private String role;
    private String permission;

    public void setUserClass(final Class<?> userClass) {
        this.userClass = userClass;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public void setPermission(final String permission) {
        this.permission = permission;
    }

    private SubjectManager getSubjectManager() {
        final ApplicationContext context = SpringWebUtil.getApplicationContext(this.pageContext);
        return SpringUtil.getFirstBeanByClass(context, SecurityManager.class);
    }

    @Override
    public int doStartTag() throws JspException {
        final HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        final HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
        final Subject subject = getSubjectManager().getSubject(request, response, this.userClass);
        if (subject != null && subject.isAuthorized(new Authority(this.role, this.permission))) {
            return Tag.EVAL_BODY_INCLUDE;
        }
        return Tag.SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return Tag.EVAL_PAGE;
    }

}
