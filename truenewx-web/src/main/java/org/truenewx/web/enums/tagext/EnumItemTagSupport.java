package org.truenewx.web.enums.tagext;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.context.ApplicationContext;
import org.truenewx.core.enums.support.EnumDictResolver;
import org.truenewx.core.enums.support.EnumItem;
import org.truenewx.core.enums.support.EnumType;
import org.truenewx.web.spring.util.SpringWebUtil;
import org.truenewx.web.tagext.ItemTagSupport;

/**
 * 基于枚举选项的标签支持
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class EnumItemTagSupport extends ItemTagSupport {
    protected String type;
    protected String subtype;

    public final void setType(final String type) throws JspException {
        this.type = getElExpressionValue("type", type, String.class);
    }

    public final void setSubtype(final String subtype) throws JspException {
        this.subtype = getElExpressionValue("subtype", subtype, String.class);
    }

    @Override
    public final void doTag() throws JspException, IOException {
        final ApplicationContext context = SpringWebUtil.getApplicationContext(getPageContext());
        final EnumDictResolver enumDictResolver = context.getBean(EnumDictResolver.class);
        final EnumType enumType = enumDictResolver.getEnumType(this.type, this.subtype,
                        getLocale());
        if (enumType != null) {
            this.items = enumType.getItems();
            super.doTag();
        }
    }

    @Override
    protected String getItemValue(final Object item) {
        return ((EnumItem) item).getKey();
    }

    @Override
    protected String getItemText(final Object item) {
        return ((EnumItem) item).getCaption();
    }

}
