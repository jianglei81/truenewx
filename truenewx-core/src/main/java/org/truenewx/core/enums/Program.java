package org.truenewx.core.enums;

import org.truenewx.core.annotation.Caption;
import org.truenewx.core.enums.annotation.EnumValue;

/**
 * 程序类型
 *
 * @author jianglei
 * @since JDK 1.7
 */
public enum Program {

    @Caption("网页")
    @EnumValue("W")
    WEB,

    @Caption("应用")
    @EnumValue("A")
    APP;

}
