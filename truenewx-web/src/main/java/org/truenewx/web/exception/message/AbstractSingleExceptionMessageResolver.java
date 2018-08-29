package org.truenewx.web.exception.message;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * 基于Spring的抽象单异常消息解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractSingleExceptionMessageResolver
                implements SingleExceptionMessageResolver, MessageSourceAware {

    protected MessageSource messageSource;

    @Override
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
