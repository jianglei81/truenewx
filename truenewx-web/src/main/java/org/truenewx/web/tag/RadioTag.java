package org.truenewx.web.tag;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.web.tagext.ItemTagSupport;

/**
 * 单选框标签
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class RadioTag extends ItemTagSupport {

    @Override
    protected void resolveItem(final String value, final String text) throws IOException {
        print("<input type=\"radio\"");
        final String name = getName();
        if (StringUtils.isNotBlank(name)) {
            print(" name=\"", name, "\"");
        }
        final String id = getId();
        if (StringUtils.isNotBlank(id)) {
            print(Strings.SPACE, id, Strings.UNDERLINE, value);
        }
        print(" value=\"", value, "\"");
        print(joinAttributes("id", "name", "value"));
        if (isCurrentValue(value)) {
            print(" checked=\"checked\"");
        }
        print("/>", text, Strings.ENTER);
    }

}
