package org.truenewx.web.tagext;

import javax.servlet.jsp.tagext.TagSupport;

import org.truenewx.core.exception.HandleableException;

/**
 * 错误标签支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ErrorTagSupport extends TagSupport {

    private static final long serialVersionUID = 3177238540767486964L;
    /**
     * 存放业务异常的关键字
     */
    public static final String EXCEPTION_KEY = HandleableException.class.getName();

    protected String field;

    public void setfield(final String field) {
        this.field = field;
    }

    protected final Object getException() {
        return this.pageContext.getRequest().getAttribute(EXCEPTION_KEY);
    }

}
