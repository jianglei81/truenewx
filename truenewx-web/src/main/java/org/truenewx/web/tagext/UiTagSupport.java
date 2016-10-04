package org.truenewx.web.tagext;

import javax.servlet.jsp.JspException;

/**
 * UI标签支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UiTagSupport extends SimpleDynamicAttributeTagSupport {

    public void setId(final String id) throws JspException {
        setDynamicAttribute(null, "id", id);
    }

    public void setName(final String name) throws JspException {
        setDynamicAttribute(null, "name", name);
    }

    public void setClassName(final String className) throws JspException {
        setDynamicAttribute(null, "class", className);
    }

    public void setStyle(final String style) throws JspException {
        setDynamicAttribute(null, "style", style);
    }

    public void setTitle(final String title) throws JspException {
        setDynamicAttribute(null, "title", title);
    }

    public void setDisabled(final String disabled) throws JspException {
        setDynamicAttribute(null, "disabled", disabled);
    }

    public void setTabIndex(final String tabIndex) throws JspException {
        setDynamicAttribute(null, "tabIndex", tabIndex);
    }

    public void setPlaceholder(final String placeholder) throws JspException {
        setDynamicAttribute(null, "placeholder", placeholder);
    }

    public void setOnclick(final String onclick) throws JspException {
        setDynamicAttribute(null, "onclick", onclick);
    }

    public void setOndblclick(final String ondblclick) throws JspException {
        setDynamicAttribute(null, "ondblclick", ondblclick);
    }

    public void setOnmousedown(final String onmousedown) throws JspException {
        setDynamicAttribute(null, "onmousedown", onmousedown);
    }

    public void setOnmouseup(final String onmouseup) throws JspException {
        setDynamicAttribute(null, "onmouseup", onmouseup);
    }

    public void setOnmouseover(final String onmouseover) throws JspException {
        setDynamicAttribute(null, "onmouseover", onmouseover);
    }

    public void setOnmousemove(final String onmousemove) throws JspException {
        setDynamicAttribute(null, "onmousemove", onmousemove);
    }

    public void setOnmouseout(final String onmouseout) throws JspException {
        setDynamicAttribute(null, "onmouseout", onmouseout);
    }

    public void setOnfocus(final String onfocus) throws JspException {
        setDynamicAttribute(null, "onfocus", onfocus);
    }

    public void setOnblur(final String onblur) throws JspException {
        setDynamicAttribute(null, "onblur", onblur);
    }

    public void setOnkeypress(final String onkeypress) throws JspException {
        setDynamicAttribute(null, "onkeypress", onkeypress);
    }

    public void setOnkeydown(final String onkeydown) throws JspException {
        setDynamicAttribute(null, "onkeydown", onkeydown);
    }

    public void setOnkeyup(final String onkeyup) throws JspException {
        setDynamicAttribute(null, "onkeyup", onkeyup);
    }

    public void setOnselect(final String onselect) throws JspException {
        setDynamicAttribute(null, "onselect", onselect);
    }

    public void setOnchange(final String onchange) throws JspException {
        setDynamicAttribute(null, "onchange", onchange);
    }

    protected String getId() {
        return (String) this.attributes.get("id");
    }

    protected String getName() {
        return (String) this.attributes.get("name");
    }

}
