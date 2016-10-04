package org.truenewx.web.tag;

import java.io.IOException;

import org.truenewx.core.Strings;
import org.truenewx.web.tagext.ItemTagSupport;

/**
 * 下拉框标签
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class SelectTag extends ItemTagSupport {

    @Override
    protected void resolveItems(final Iterable<?> items) throws IOException {
        print("<select", joinAttributes(), ">", Strings.ENTER);
        super.resolveItems(items);
        print("</select>", Strings.ENTER);
    }

    @Override
    protected void resolveItem(final String value, final String text) throws IOException {
        print("  <option value=", Strings.DOUBLE_QUOTES, value, Strings.DOUBLE_QUOTES);
        if (isCurrentValue(value)) {
            print(" selected=\"selected\"");
        }
        print(">", text, "</option>", Strings.ENTER);
    }
}
