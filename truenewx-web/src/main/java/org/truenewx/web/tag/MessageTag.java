package org.truenewx.web.tag;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.LocaleResolver;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.spring.util.SpringWebUtil;

/**
 * 国际化消息标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MessageTag extends SimpleTagSupport {

    private String code;
    private String[] args;
    private ApplicationContext context;

    public void setCode(final String code) {
        this.code = code;
    }

    public void setArgs(final String args) {
        this.args = args.trim().split(Strings.COMMA);
    }

    private ApplicationContext getApplicationContext() {
        if (this.context == null) {
            this.context = SpringWebUtil.getApplicationContext((PageContext) getJspContext());
        }
        return this.context;
    }

    private Locale getLocale() {
        final LocaleResolver localeResolver = SpringUtil
                .getFirstBeanByClass(getApplicationContext(), LocaleResolver.class);
        final HttpServletRequest request = (HttpServletRequest) ((PageContext) getJspContext())
                .getRequest();
        if (localeResolver != null) {
            return localeResolver.resolveLocale(request);
        } else {
            return request.getLocale();
        }
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (StringUtils.isNotBlank(this.code)) {
            final String message = getApplicationContext().getMessage(this.code, this.args, null,
                    getLocale());
            if (message != null) {
                getJspContext().getOut().print(message);
            }
        }
    }

}
