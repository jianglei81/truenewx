package org.truenewx.core.exception.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.core.exception.SingleException;
import org.truenewx.core.spring.beans.ContextInitializedBean;

/**
 * 复合的单异常消息解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
// 作为默认的单异常消息解决器，不要改动下列组件beanId
@Component("singleExceptionMessageResolver")
public class CompositeSingleExceptionMessageResolver
                implements SingleExceptionMessageResolver, ContextInitializedBean {

    private List<SingleExceptionMessageResolver> resolvers;

    public void setResolvers(final List<SingleExceptionMessageResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        if (this.resolvers == null) {
            final Map<String, SingleExceptionMessageResolver> resolvers = context
                            .getBeansOfType(SingleExceptionMessageResolver.class);
            if (!resolvers.isEmpty()) {
                this.resolvers = new ArrayList<>();
                for (final SingleExceptionMessageResolver resolver : resolvers.values()) {
                    if (resolver != this) { // 防止加入自身
                        this.resolvers.add(resolver);
                    }
                }
            }
        }
    }

    @Override
    public String resolveMessage(final SingleException se, final Locale locale) {
        if (this.resolvers != null) {
            for (final SingleExceptionMessageResolver resolver : this.resolvers) {
                final String message = resolver.resolveMessage(se, locale);
                if (message != null) {
                    return message;
                }
            }
        }
        return null;
    }

}
