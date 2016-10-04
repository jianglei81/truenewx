package org.truenewx.core.enums;

import org.truenewx.core.annotation.Caption;

/**
 * 数值偏差
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum Deviation {

    @Caption("无偏差") NONE,

    @Caption("偏小于") LESS,

    @Caption("偏大于") GREATER,

    @Caption("约等于") AROUND;

}
