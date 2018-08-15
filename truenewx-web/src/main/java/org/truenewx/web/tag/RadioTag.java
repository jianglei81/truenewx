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
    protected void resolveItem(String value, String text) throws IOException {
        print("<input type=\"radio\"");
        String name = getName();
        if (StringUtils.isNotBlank(name)) {
            print(" name=\"", name, "\"");
        }
        String id = getId();
        if (StringUtils.isNotBlank(id)) {
            print(Strings.SPACE, id, Strings.UNDERLINE, value);
        }
        print(" value=\"", value, "\"");
        print(joinAttributes("id", "name", "value"));
        if (isSelectedValue(value)) {
            print(" checked=\"checked\"");
        }
        print("/>", text, Strings.ENTER);
    }

}
