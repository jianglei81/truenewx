package org.truenewx.web.rpc.server;

import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.enums.support.EnumDictResolver;
import org.truenewx.core.enums.support.EnumItem;
import org.truenewx.core.enums.support.EnumType;
import org.truenewx.web.spring.context.SpringWebContext;

/**
 * RPC控制器支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class RpcControllerSupport {

    @Autowired
    private EnumDictResolver enumDictResolver;

    protected Collection<EnumItem> getEnumItems(Class<? extends Enum<?>> enumClass,
            String subtype) {
        Locale locale = SpringWebContext.getLocale();
        EnumType enumType = this.enumDictResolver.getEnumType(enumClass.getName(), subtype, locale);
        if (enumType == null) {
            String message = "No such enum type: " + enumClass.getName();
            if (StringUtils.isNotBlank(subtype)) {
                message += " for subtype '" + subtype + "'";
            }
            throw new IllegalArgumentException(message);
        }
        return enumType.getItems();
    }

}
