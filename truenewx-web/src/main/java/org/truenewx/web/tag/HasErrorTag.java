package org.truenewx.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.truenewx.core.exception.MultiException;
import org.truenewx.core.exception.SingleException;
import org.truenewx.web.tagext.ErrorTagSupport;

/**
 * 是否存在错误消息的标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class HasErrorTag extends ErrorTagSupport {

    private static final long serialVersionUID = -8236304660577964951L;

    @Override
    public int doStartTag() throws JspException {
        final Object obj = getException();
        if (obj != null) {
            if (obj instanceof SingleException) {
                final SingleException se = (SingleException) obj;
                if (se.matches(this.field)) {
                    return Tag.EVAL_BODY_INCLUDE;
                }
            } else if (obj instanceof MultiException) {
                final MultiException me = (MultiException) obj;
                if (me.containsPropertyException(this.field)) {
                    return Tag.EVAL_BODY_INCLUDE;
                }
            }
        }
        return Tag.SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return Tag.EVAL_PAGE;
    }
}
