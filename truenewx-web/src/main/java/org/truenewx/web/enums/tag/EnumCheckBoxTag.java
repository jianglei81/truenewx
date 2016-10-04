package org.truenewx.web.enums.tag;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.web.enums.tagext.EnumItemTagSupport;

/**
 * 基于枚举的复选框标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumCheckBoxTag extends EnumItemTagSupport {

    public void setValues(final Enum<?>[] value) {
        final String[] array = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = value[i].name();
        }
        super.setValue(array);
    }

    @Override
    protected boolean isCurrentValue(final String value) {
        final boolean result = super.isCurrentValue(value);
        if (!result) {
            String[] values;
            if (this.value instanceof String) {
                values = ((String) this.value).split(Strings.COMMA);
            } else if (this.value instanceof String[]) {
                values = (String[]) this.value;
            } else {
                return false;
            }
            return ArrayUtils.contains(values, value);
        }
        return result;
    }

    @Override
    protected void resolveItem(final String value, final String text) throws IOException {
        print("<input type=\"checkbox\"");
        final String id = getId();
        if (StringUtils.isNotBlank(id)) {
            print(Strings.SPACE, "id=\"", id, Strings.UNDERLINE, value, "\"");
        }
        print(Strings.SPACE, "value=\"", value, "\"");
        print(joinAttributes("id", "value"));
        if (isCurrentValue(value)) {
            print(" checked=\"checked\"");
        }
        print("/> ", text, Strings.ENTER);
    }

}
