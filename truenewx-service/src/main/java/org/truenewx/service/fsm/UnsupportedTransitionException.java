package org.truenewx.service.fsm;

import org.truenewx.core.exception.BusinessException;

/**
 * UnsupportedTransitionException
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UnsupportedTransitionException extends BusinessException {

    private static final long serialVersionUID = 3918018972788204325L;
    /**
     * 错误码：指定转换错误
     */
    public static final String CODE_SPECIFIED_TRANSITION = "error.fsm.unsupported_specified_transition";

    public UnsupportedTransitionException(final Enum<?> state, final Enum<?> transition) {
        super(CODE_SPECIFIED_TRANSITION, state, transition);
    }

}
