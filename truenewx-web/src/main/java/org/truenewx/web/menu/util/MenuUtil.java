package org.truenewx.web.menu.util;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;

/**
 * 菜单工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuUtil {

    private MenuUtil() {
    }

    public static String getAuthority(final Enum<?> enumConstant) {
        return StringUtils.join(enumConstant.getClass().getSimpleName(), Strings.DOT,
                        enumConstant.name());
    }

}
