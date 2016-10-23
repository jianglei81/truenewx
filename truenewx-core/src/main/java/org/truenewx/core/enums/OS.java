package org.truenewx.core.enums;

import org.truenewx.core.annotation.Caption;
import org.truenewx.core.enums.annotation.EnumValue;

/**
 * 操作系统类型
 *
 * @author jianglei
 * @since JDK 1.7
 */
public enum OS {

    @Caption("Windows")
    @EnumValue("W")
    WINDOWS, @Caption("Android")
    @EnumValue("A")
    ANDROID, @Caption("MAC")
    @EnumValue("M")
    MAC, @Caption("所有")
    @EnumValue("L")
    ALL;
}
