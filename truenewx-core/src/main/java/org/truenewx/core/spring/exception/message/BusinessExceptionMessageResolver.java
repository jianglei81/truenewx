package org.truenewx.core.spring.exception.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.core.enums.support.EnumDictResolver;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.SingleException;

/**
 * Spring的业务异常消息解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class BusinessExceptionMessageResolver extends AbstractSingleExceptionMessageResolver {

    private EnumDictResolver enumDictResolver;

    @Autowired(required = false)
    public void setEnumDictResolver(final EnumDictResolver enumDictResolver) {
        this.enumDictResolver = enumDictResolver;
    }

    @Override
    public String resolveMessage(final SingleException se, final Locale locale) {
        if (se instanceof BusinessException) {
            final BusinessException be = (BusinessException) se;
            final Object[] args = be.getArgs();
            if (this.enumDictResolver != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Enum) {
                        args[i] = this.enumDictResolver.getText((Enum<?>) args[i], locale);
                    }
                }
            }
            return this.messageSource.getMessage(be.getCode(), args, be.getCode(), locale);
        }
        return null;
    }

}
