package org.truenewx.web.enums.tagext;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.truenewx.core.Strings;
import org.truenewx.core.enums.support.EnumDictResolver;
import org.truenewx.core.enums.support.EnumItem;
import org.truenewx.core.enums.support.EnumType;
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

    public void setType(String type) throws JspException {
        this.type = getElExpressionValue("type", type, String.class);
    }

    public void setSubtype(String subtype) throws JspException {
        this.subtype = getElExpressionValue("subtype", subtype, String.class);
    }

    @Override
    public void doTag() throws JspException, IOException {
        EnumDictResolver enumDictResolver = getBeanFromApplicationContext(EnumDictResolver.class);
        EnumType enumType = enumDictResolver.getEnumType(this.type, this.subtype, getLocale());
        if (enumType != null) {
            this.items = enumType.getItems();
            super.doTag();
        }
    }

    @Override
    protected boolean isCurrentValue(Object value) {
        if (this.value == null) {
            this.value = Strings.EMPTY;
        }
        return this.value.toString().equals(value.toString());
    }

    @Override
    protected String getItemValue(Object item) {
        return ((EnumItem) item).getKey();
    }

    @Override
    protected String getItemText(Object item) {
        return ((EnumItem) item).getCaption();
    }

}
