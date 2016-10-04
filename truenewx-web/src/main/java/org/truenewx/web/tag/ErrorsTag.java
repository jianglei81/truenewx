package org.truenewx.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;

import org.springframework.context.ApplicationContext;
import org.truenewx.core.exception.MultiException;
import org.truenewx.core.exception.SingleException;
import org.truenewx.core.exception.message.SingleExceptionMessageResolver;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.spring.util.SpringWebUtil;
import org.truenewx.web.tagext.ErrorTagSupport;

/**
 * 输出错误消息的标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ErrorsTag extends ErrorTagSupport {

    private static final long serialVersionUID = -8236304660577964951L;

    private String delimiter = "<br/>";

    private String suffix;

    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    @Override
    public int doEndTag() throws JspException {
        final Object obj = getException();
        if (obj != null) {
            final ApplicationContext context = SpringWebUtil
                            .getApplicationContext(this.pageContext);
            final SingleExceptionMessageResolver messageResolver = SpringUtil
                            .getBeanByDefaultName(context, SingleExceptionMessageResolver.class);

            final StringBuffer message = new StringBuffer();
            if (obj instanceof SingleException) {
                final SingleException se = (SingleException) obj;
                if (se.matches(this.field)) {
                    message.append(messageResolver.resolveMessage(se,
                                    this.pageContext.getRequest().getLocale()));
                }
            } else if (obj instanceof MultiException) {
                final MultiException me = (MultiException) obj;
                // 遍历同一filed中的多个异常信息
                for (final SingleException se : me) {
                    if (se.matches(this.field)) {
                        message.append(messageResolver.resolveMessage(se,
                                        this.pageContext.getRequest().getLocale()));
                        message.append(this.delimiter);
                    }
                }
                if (message.length() > 0) {
                    message.delete(message.length() - this.delimiter.length(), message.length());
                }
            }
            if (message.length() > 0 && this.suffix != null) { // 有内容时才加后缀
                message.append(this.suffix);
            }

            final JspWriter out = this.pageContext.getOut();
            try {
                out.print(message);
            } catch (final IOException e) {
                throw new JspException(e);
            }
        }
        return Tag.EVAL_PAGE;
    }
}
