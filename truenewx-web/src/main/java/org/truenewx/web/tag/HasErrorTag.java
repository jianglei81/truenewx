package org.truenewx.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

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
        return matches() ? Tag.EVAL_BODY_INCLUDE : Tag.SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return Tag.EVAL_PAGE;
    }
}
