package org.truenewx.web.enums.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.truenewx.core.enums.support.EnumDictResolver;
import org.truenewx.web.tagext.UiTagSupport;

/**
 * 枚举文本标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumTextTag extends UiTagSupport {
    private String type;
    private String subtype;
    private String value;

    public void setType(final String type) throws JspException {
        this.type = getElExpressionValue("type", type, String.class);
    }

    public void setSubtype(final String subtype) throws JspException {
        this.subtype = getElExpressionValue("subtype", subtype, String.class);
    }

    public void setValue(final String value) throws JspException {
        this.value = getElExpressionValue("value", value, String.class);
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (this.value != null) {
            final EnumDictResolver enumDictResolver = getBeanFromApplicationContext(
                            EnumDictResolver.class);
            final String caption = enumDictResolver.getText(this.type, this.subtype, this.value,
                            getLocale());
            if (caption != null) {
                print(caption);
            }
        }
    }

}
