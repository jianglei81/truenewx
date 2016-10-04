package org.truenewx.web.spring.util.tag;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.context.ApplicationContext;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.spring.util.SpringWebUtil;

/**
 * 占位符输出标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PlaceholderTag extends SimpleTagSupport {

    /**
     * 占位符关键字
     */
    private String key;
    /**
     * 默认值
     */
    private String value;

    private PlaceholderResolver placeholderResolver;

    public void setKey(final String key) {
        this.key = key;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public void setJspContext(final JspContext jspContext) {
        super.setJspContext(jspContext);
        final ApplicationContext context = SpringWebUtil
                        .getApplicationContext((PageContext) jspContext);
        this.placeholderResolver = SpringUtil.getFirstBeanByClass(context,
                        PlaceholderResolver.class);
    }

    @Override
    public void doTag() throws JspException, IOException {
        String value = this.placeholderResolver.resolvePlaceholder(this.key);
        if (value == null) {
            value = this.value;
        }
        if (value != null) {
            getJspContext().getOut().print(value);
        }
    }

}
