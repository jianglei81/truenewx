package org.truenewx.web.tagext;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.exception.MultiException;
import org.truenewx.core.exception.SingleException;

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
    private String code;

    public void setField(String field) {
        this.field = field;
    }

    public void setCode(String code) {
        this.code = code;
    }

    protected final Object getException() {
        return this.pageContext.getRequest().getAttribute(EXCEPTION_KEY);
    }

    protected final boolean matches() {
        Object obj = getException();
        if (obj != null) {
            if (obj instanceof SingleException) {
                SingleException se = (SingleException) obj;
                if (se.matches(this.field)) {
                    if (StringUtils.isNotBlank(this.code) && se instanceof BusinessException) {
                        BusinessException be = (BusinessException) se;
                        return this.code.equals(be.getCode());
                    }
                    return true;
                }
            } else if (obj instanceof MultiException) {
                MultiException me = (MultiException) obj;
                if (me.containsPropertyException(this.field)) {
                    return true;
                }
            }
        }
        return false;
    }

}
