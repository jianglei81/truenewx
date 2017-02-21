package org.truenewx.core.enums;

import org.truenewx.core.annotation.Caption;
import org.truenewx.core.enums.annotation.EnumValue;

/**
 * 设备类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum Device {

    @Caption("电脑")
    @EnumValue("C")
    PC, @Caption("手机")
    @EnumValue("M")
    MOBILE, @Caption("平板")
    @EnumValue("P")
    PAD;
}
