package org.truenewx.web.spring.servlet.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.truenewx.core.exception.FormatException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.exception.MultiException;
import org.truenewx.core.functor.algorithm.impl.AlgoFirst;
import org.truenewx.core.spring.exception.message.FormatExceptionMessageResolver;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.web.tagext.ErrorTagSupport;
import org.truenewx.web.util.WebUtil;

/**
 * 字段校验异常处理器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ConstraintViolationExceptionResolver extends AbstractHandlerExceptionResolver
        implements MessageSourceAware {

    private String errorPage = "error-validate";

    protected MessageSource messageSource;

    @Override
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 异常消息解析器
     */
    private FormatExceptionMessageResolver messageResolver;

    @Autowired
    public void setMessageResolver(final FormatExceptionMessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    public void setErrorPage(final String errorPage) {
        this.errorPage = errorPage;
    }

    @Override
    protected ModelAndView doResolveException(final HttpServletRequest request,
            final HttpServletResponse response, final Object handler, final Exception ex) {
        if (ex instanceof ConstraintViolationException) {
            final ConstraintViolationException e = (ConstraintViolationException) ex;
            final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
            if (violations != null && violations.size() > 0) {
                if (WebUtil.isAjaxRequest(request)) { // AJAX请求
                    final List<BusinessError> errors = new ArrayList<>();
                    if (violations.size() == 1) {
                        final ConstraintViolation<?> violation = AlgoFirst.visit(violations, null);
                        final FormatException fe = buildFormatException(violation, request);
                        final String message = this.messageResolver.resolveMessage(fe,
                                request.getLocale());
                        errors.add(new BusinessError(fe.getBeanClass().getName(), message));
                    } else {
                        for (final ConstraintViolation<?> violation : violations) {
                            final FormatException fe = buildFormatException(violation, request);
                            final String message = this.messageResolver.resolveMessage(fe,
                                    request.getLocale());
                            errors.add(new BusinessError(fe.getBeanClass().getName(), message));
                        }
                    }
                    if (!errors.isEmpty()) {
                        try {
                            final Map<String, Object> map = new HashMap<>();
                            map.put("errors", errors);
                            response.getWriter().print(JsonUtil.toJson(map));
                            response.setStatus(500);
                            return new ModelAndView();
                        } catch (final IOException iex) {
                            iex.printStackTrace();
                        }
                    }
                } else {
                    if (violations.size() == 1) {
                        final ConstraintViolation<?> violation = AlgoFirst.visit(violations, null);
                        final FormatException fe = buildFormatException(violation, request);
                        applyException(request, fe);
                    } else {
                        final MultiException me = new MultiException();
                        for (final ConstraintViolation<?> violation : violations) {
                            me.add(buildFormatException(violation, request));
                        }
                        applyException(request, me);
                    }
                    return new ModelAndView(this.errorPage);
                }
            }
        }
        return null;
    }

    private FormatException buildFormatException(final ConstraintViolation<?> violation,
            final HttpServletRequest request) {
        final String property = violation.getPropertyPath().toString();
        final String message = this.messageSource.getMessage(
                violation.getMessage().replace("{", "").replace("}", ""), null,
                violation.getMessage(), request.getLocale());
        return new FormatException(violation.getRootBeanClass(), property, message);
    }

    private void applyException(final HttpServletRequest request, final HandleableException e) {
        request.setAttribute(ErrorTagSupport.EXCEPTION_KEY, e);
    }

}
