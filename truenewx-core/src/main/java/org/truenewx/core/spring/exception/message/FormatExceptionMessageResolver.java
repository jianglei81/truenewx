package org.truenewx.core.spring.exception.message;

import java.util.Locale;

import org.springframework.stereotype.Component;
import org.truenewx.core.exception.FormatException;
import org.truenewx.core.exception.SingleException;

/**
 * 格式异常消息解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class FormatExceptionMessageResolver extends AbstractSingleExceptionMessageResolver {

    @Override
    public String resolveMessage(final SingleException se, final Locale locale) {
        if (se instanceof FormatException) {
            final FormatException fe = (FormatException) se;
            final String propertyText = getPropertyText(fe, locale);
            return propertyText + fe.getMessage();
        }
        return null;
    }

    private String getPropertyText(final FormatException fe, final Locale locale) {
        String text = getPropertyPathText(fe.getFullPropertyPath(), locale); // 先尝试取完全路径的
        if (text == null) {
            final String simplePropertyPath = fe.getSimplePropertyPath();
            text = getPropertyPathText(simplePropertyPath, locale); // 再尝试取简短路径的

            if (text == null) {
                text = getPropertyPathText(fe.getProperty(), locale); // 最后尝试取仅属性的

                if (text == null) {
                    text = simplePropertyPath; // 如果均未取到，则返回简短路径
                }
            }
        }
        return text;
    }

    private String getPropertyPathText(final String propertyPath, final Locale locale) {
        final String text = this.messageSource.getMessage(propertyPath, null, null, locale);
        return propertyPath.equals(text) ? null : text;
    }
}
