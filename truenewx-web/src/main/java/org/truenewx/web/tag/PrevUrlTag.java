package org.truenewx.web.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.context.ApplicationContext;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.spring.context.SpringWebContext;
import org.truenewx.web.spring.servlet.mvc.Loginer;
import org.truenewx.web.spring.util.SpringWebUtil;
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
    private boolean withContext = true;

    public void setDefault(final String defaultHref) {
        this.defaultHref = defaultHref;
    }

    public void setContext(final boolean context) {
        this.withContext = context;
    }

    @Override
    public int doEndTag() throws JspException {
        // 使用pageContext中的request会得到jsp页面的访问路径，这可能导致错误
        final HttpServletRequest request = SpringWebContext.getRequest();
        final String currentAction = WebUtil.getRelativeRequestAction(request);
        String prevUrl = WebUtil.getRelativePreviousUrl(request, true);
        if (prevUrl != null) {
            if (prevUrl.startsWith(currentAction)) { // 如果前一页url以当前action开头，则执行默认的前一页规则，以避免跳转相同页
                prevUrl = null;
            } else {
                final ApplicationContext context = SpringWebUtil.getApplicationContext(request);
                final Loginer loginer = SpringUtil.getFirstBeanByClass(context, Loginer.class);
                if (loginer != null && prevUrl != null && loginer.isLoginUrl(prevUrl)) {
                    prevUrl = null;
                }
            }
        }
        final JspWriter out = this.pageContext.getOut();
        try {
            if (prevUrl != null) {
                if (this.withContext) {
                    final String contextPath = request.getContextPath();
                    if (!contextPath.equals(Strings.SLASH)) {
                        out.print(contextPath);
                    }
                }
                out.print(prevUrl);
            } else {
                out.print(this.defaultHref);
            }
        } catch (final IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

}
