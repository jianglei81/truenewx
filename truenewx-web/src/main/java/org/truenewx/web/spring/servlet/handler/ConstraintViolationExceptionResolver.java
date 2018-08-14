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
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 异常消息解析器
     */
    private FormatExceptionMessageResolver messageResolver;

    @Autowired
    public void setMessageResolver(FormatExceptionMessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException e = (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
            if (violations != null && violations.size() > 0) {
                if (WebUtil.isAjaxRequest(request)) { // AJAX请求
                    List<HandledError> errors = new ArrayList<>();
                    if (violations.size() == 1) {
                        ConstraintViolation<?> violation = AlgoFirst.visit(violations, null);
                        FormatException fe = buildFormatException(violation, request);
                        String message = this.messageResolver.resolveMessage(fe,
                                request.getLocale());
                        errors.add(new HandledError(fe.getBeanClass().getName(), message));
                    } else {
                        for (ConstraintViolation<?> violation : violations) {
                            FormatException fe = buildFormatException(violation, request);
                            String message = this.messageResolver.resolveMessage(fe,
                                    request.getLocale());
                            errors.add(new HandledError(fe.getBeanClass().getName(), message));
                        }
                    }
                    if (!errors.isEmpty()) {
                        try {
                            Map<String, Object> map = new HashMap<>();
                            map.put("errors", errors);
                            response.getWriter().print(JsonUtil.toJson(map));
                            response.setStatus(HandledError.SC_HANDLED_ERROR);
                            return new ModelAndView();
                        } catch (IOException iex) {
                            this.logger.error(iex.getMessage(), iex);
                        }
                    }
                } else {
                    if (violations.size() == 1) {
                        ConstraintViolation<?> violation = AlgoFirst.visit(violations, null);
                        FormatException fe = buildFormatException(violation, request);
                        applyException(request, fe);
                    } else {
                        MultiException me = new MultiException();
                        for (ConstraintViolation<?> violation : violations) {
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

    private FormatException buildFormatException(ConstraintViolation<?> violation,
            HttpServletRequest request) {
        String property = violation.getPropertyPath().toString();
        String message = this.messageSource.getMessage(
                violation.getMessage().replace("{", "").replace("}", ""), null,
                violation.getMessage(), request.getLocale());
        return new FormatException(violation.getRootBeanClass(), property, message);
    }

    private void applyException(HttpServletRequest request, HandleableException e) {
        request.setAttribute(ErrorTagSupport.EXCEPTION_KEY, e);
    }

}
