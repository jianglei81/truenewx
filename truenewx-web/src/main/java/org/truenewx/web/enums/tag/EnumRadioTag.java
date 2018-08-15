package org.truenewx.web.enums.tag;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.web.enums.tagext.EnumItemTagSupport;

/**
 * 基于枚举的单选框标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumRadioTag extends EnumItemTagSupport {

    @Override
    protected String getName() {
        String name = super.getName();
        if (StringUtils.isBlank(name)) {
            name = getId();
            if (StringUtils.isBlank(name)) {
                if (StringUtils.isBlank(this.subtype)) {
                    name = this.type;
                } else {
                    name = StringUtils.join(this.type, Strings.UNDERLINE, this.subtype);
                }
            }
        }
        return name;
    }

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
        if (isCurrentValue(value)) {
            print(" checked=\"checked\"");
        }
        print("/>", text, Strings.ENTER);
    }

}
