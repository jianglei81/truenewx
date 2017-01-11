package org.truenewx.web.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.truenewx.core.Strings;
import org.truenewx.web.util.WebUtil;

/**
 * 输出前一个请求URL的标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PrevUrlTag extends TagSupport {

    private static final long serialVersionUID = 8123676932054182255L;

    private String defaultHref = "javascript:history.back(-1)";

    public void setDefault(final String defaultHref) {
        this.defaultHref = defaultHref;
    }

    @Override
    public int doEndTag() throws JspException {
        final HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
        final String previousUrl = WebUtil.getRelativePreviousUrl(request, true);
        final JspWriter out = this.pageContext.getOut();
        try {
            if (previousUrl != null) {
                final String contextPath = request.getContextPath();
                if (!contextPath.equals(Strings.SLASH)) {
                    out.print(contextPath);
                }
                out.print(previousUrl);
            } else {
                out.print(this.defaultHref);
            }
        } catch (final IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

}
