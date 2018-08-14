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
    protected void resolveItems(Iterable<?> items) throws IOException {
        print("<select", joinAttributes(), ">", Strings.ENTER);
        beforeFirstItem();
        super.resolveItems(items);
        afterLastItem();
        print("</select>", Strings.ENTER);
    }

    @Override
    protected void resolveItem(String value, String text) throws IOException {
        print("  <option value=", Strings.DOUBLE_QUOTES, value, Strings.DOUBLE_QUOTES);
        if (isCurrentValue(value)) {
            print(" selected=\"selected\"");
        }
        print(">", text, "</option>", Strings.ENTER);
    }

    /**
     * 在生成第一个条目之前调用，子类覆写可用于生成额外的条目
     */
    protected void beforeFirstItem() throws IOException {
    }

    /**
     * 在生成最后一条目之后调用，子类覆写可用于生成额外的条目
     */
    protected void afterLastItem() throws IOException {
    }

}
