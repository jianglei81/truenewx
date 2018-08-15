package org.truenewx.web.tagext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.enums.support.EnumDictResolver;
import org.truenewx.core.util.BeanUtil;

/**
 * 选项标签支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class ItemTagSupport extends UiTagSupport {
    protected Object items;
    protected Object value;
    protected boolean emptyItem;
    protected String emptyItemValue = "";
    protected String emptyItemText = "&nbsp;";
    protected String itemValueProperty;
    protected String itemTextProperty;
    protected String separator;

    public final void setItems(final Object items) {
        this.items = items;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public final void setEmptyItem(final String emptyItem) throws JspException {
        this.emptyItem = getElExpressionValue("emptyItem", emptyItem, Boolean.class);
    }

    public final void setEmptyItemValue(final String emptyItemValue) throws JspException {
        this.emptyItemValue = getElExpressionValue("emptyItemValue", emptyItemValue, String.class);
    }

    public final void setEmptyItemText(final String emptyItemText) throws JspException {
        this.emptyItemText = getElExpressionValue("emptyItemText", emptyItemText, String.class);
    }

    public final void setItemValueProperty(final String itemValueProperty) throws JspException {
        this.itemValueProperty = getElExpressionValue("itemValueProperty", itemValueProperty,
                String.class);
    }

    public final void setItemTextProperty(final String itemTextProperty) throws JspException {
        this.itemTextProperty = getElExpressionValue("itemTextProperty", itemTextProperty,
                String.class);
    }

    public final void setSeparator(final String separator) throws JspException {
        this.separator = getElExpressionValue("separator", separator, String.class);
    }

    @Override
    public void doTag() throws JspException, IOException {
        Iterable<?> items = null;
        if (this.items instanceof Map<?, ?>) {
            items = ((Map<?, ?>) this.items).entrySet();
        } else if (this.items instanceof Iterable<?>) {
            items = (Iterable<?>) this.items;
        } else if (this.items instanceof Object[]) {
            items = Arrays.asList((Object[]) this.items);
        }
        resolveItems(items);
    }

    protected void resolveItems(final Iterable<?> items) throws IOException {
        if (this.emptyItem) {
            resolveItem(this.emptyItemValue, this.emptyItemText);
        }
        if (items != null) {
            int i = 0;
            for (final Object item : items) {
                if (this.separator != null && i++ > 0) { // 有分隔符且非首项，则在前面添加分隔符
                    print(this.separator);
                }
                resolveItem(item);
            }
        }
    }

    protected void resolveItem(final Object item) throws IOException {
        resolveItem(getItemValue(item), getItemText(item));
    }

    protected String getItemValue(final Object item) {
        Object value = null;
        if (item instanceof Entry) {
            value = ((Entry<?, ?>) item).getKey();
        } else if (StringUtils.isNotBlank(this.itemValueProperty)) {
            value = BeanUtil.getPropertyValue(item, this.itemValueProperty);
        } else {
            value = item;
        }
        return value == null ? null : value.toString();
    }

    protected String getItemText(final Object item) {
        Object text = null;
        if (item instanceof Entry) {
            text = ((Entry<?, ?>) item).getValue();
        } else if (item instanceof Enum) {
            final EnumDictResolver enumDictResolver = getBeanFromApplicationContext(
                    EnumDictResolver.class);
            return enumDictResolver.getText((Enum<?>) item, getLocale());
        } else if (StringUtils.isNotBlank(this.itemTextProperty)) {
            text = BeanUtil.getPropertyValue(item, this.itemTextProperty);
        } else {
            text = item;
        }
        return text == null ? null : text.toString();
    }

    /**
     * 判断指定取值是否当前选中值
     *
     * @param value
     *            取值
     * @return 指定取值是否当前值
     */
    protected boolean isSelectedValue(Object value) {
        // null等于""
        if (value == null) {
            value = Strings.EMPTY;
        }
        if (this.value == null) {
            this.value = Strings.EMPTY;
        }
        return value.equals(this.value);
    }

    protected abstract void resolveItem(String value, String text) throws IOException;

}
