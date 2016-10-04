package org.truenewx.core.enums.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.truenewx.core.tuple.Binary;
import org.truenewx.core.tuple.Binate;

/**
 * 枚举字典
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumDict {
    private Locale locale;
    private Map<Binate<String, String>, EnumType> types = new HashMap<Binate<String, String>, EnumType>();

    public EnumDict(final Locale locale) {
        if (locale == null) {
            this.locale = Locale.getDefault();
        } else {
            this.locale = locale;
        }
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void addType(final EnumType type) {
        if (type != null) {
            final Binate<String, String> key = new Binary<String, String>(type.getName(),
                            type.getSubname());
            this.types.put(key, type);
        }
    }

    public EnumType getType(final String name) {
        return getType(name, null);
    }

    public EnumType getType(final String name, final String subname) {
        final Binate<String, String> key = new Binary<String, String>(name, subname);
        return this.types.get(key);
    }
}
