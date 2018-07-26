package org.truenewx.web.security.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.security.mgt.SecurityManager;
import org.truenewx.web.security.mgt.SubjectManager;
import org.truenewx.web.security.subject.Subject;
import org.truenewx.web.spring.util.SpringWebUtil;

/**
 * 授权判断标签支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AuthorizeTagSupport extends TagSupport {

    private static final long serialVersionUID = -573915234521446453L;

    private String userClassName;
    protected String role;
    protected String permission;

    public void setUserClass(String userClass) {
        this.userClassName = userClass;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    private SubjectManager getSubjectManager() {
        ApplicationContext context = SpringWebUtil.getApplicationContext(this.pageContext);
        return SpringUtil.getFirstBeanByClass(context, SecurityManager.class);
    }

    private Class<?> getUserClass() throws ClassNotFoundException {
        if (StringUtils.isBlank(this.userClassName)) {
            return null;
        }
        ApplicationContext context = SpringWebUtil.getApplicationContext(this.pageContext);
        return context.getClassLoader().loadClass(this.userClassName);
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
            Class<?> userClass = getUserClass();
            Subject subject = getSubjectManager().getSubject(request, response, userClass);
            if (predicateAuthorize(subject)) {
                return Tag.EVAL_BODY_INCLUDE;
            }
        } catch (ClassNotFoundException e) {
            throw new JspException(e);
        }
        return Tag.SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return Tag.EVAL_PAGE;
    }

    protected abstract boolean predicateAuthorize(Subject subject);

}
