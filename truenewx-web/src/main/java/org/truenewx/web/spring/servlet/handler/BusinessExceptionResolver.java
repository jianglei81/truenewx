package org.truenewx.web.spring.servlet.handler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.truenewx.core.Strings;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.core.exception.MultiException;
import org.truenewx.core.exception.SingleException;
import org.truenewx.core.spring.exception.message.BusinessExceptionMessageResolver;
import org.truenewx.core.util.JsonUtil;
import org.truenewx.web.exception.annotation.HandleableExceptionMessage;
import org.truenewx.web.exception.annotation.HandleableExceptionResult;
import org.truenewx.web.tagext.ErrorTagSupport;
import org.truenewx.web.util.WebUtil;
import org.truenewx.web.validation.generate.HandlerValidationGenerator;
import org.truenewx.web.validation.generate.annotation.ValidationGeneratable;

/**
 * Spring的业务异常解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class BusinessExceptionResolver extends SimpleMappingExceptionResolver {
    /**
     * 响应状态码：业务错误
     */
    private static int SC_BUSINESS_ERROR = 600;

    /**
     * 业务异常消息解析器
     */
    private BusinessExceptionMessageResolver messageResolver;
    /**
     * 处理器校验生成器
     */
    private HandlerValidationGenerator handlerValidationGenerator;

    @Autowired
    public void setMessageResolver(final BusinessExceptionMessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @Autowired
    public void setHandlerValidationGenerator(
            final HandlerValidationGenerator handlerValidationGenerator) {
        this.handlerValidationGenerator = handlerValidationGenerator;
    }

    @Override
    protected ModelAndView doResolveException(final HttpServletRequest request,
            final HttpServletResponse response, final Object handler, final Exception e) {
        ModelAndView mav = null;
        if (handler instanceof HandlerMethod) {
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (isAnnotatedHandleableExceptionMessage(handlerMethod)) {
                mav = handleExceptionToMessage(e, request.getLocale(), response);
            } else if (e instanceof HandleableException) {
                mav = handleExceptionToPage(request, handlerMethod, (HandleableException) e);
            }
            final ValidationGeneratable vg = handlerMethod
                    .getMethodAnnotation(ValidationGeneratable.class);
            if (vg != null) {
                this.handlerValidationGenerator.generate(request, vg.value(), mav);
            }
        }
        if (mav == null) {
            mav = super.doResolveException(request, response, handler, e);
        }
        return mav;
    }

    private boolean isAnnotatedHandleableExceptionMessage(final HandlerMethod handlerMethod) {
        // 方法上有@HandleableExceptionMessage注解，则返回true
        return handlerMethod.getMethodAnnotation(HandleableExceptionMessage.class) != null;
    }

    /**
     * 以错误消息的方式处理异常
     *
     * @param e
     *            异常
     * @param response
     *            HTTP响应
     * @return 模型视图
     */
    private ModelAndView handleExceptionToMessage(final Exception e, final Locale locale,
            final HttpServletResponse response) {
        final List<BusinessError> errors = new ArrayList<>();
        if (e instanceof BusinessException) { // 业务异常，转换错误消息
            final BusinessException be = (BusinessException) e;
            final String message = this.messageResolver.resolveMessage(be, locale);
            errors.add(new BusinessError(be.getCode(), message, be.getProperty()));
        } else if (e instanceof MultiException) { // 业务异常集，转换错误消息
            final MultiException me = (MultiException) e;
            for (final SingleException se : me) {
                if (se instanceof BusinessException) {
                    final BusinessException be = (BusinessException) se;
                    final String message = BusinessExceptionResolver.this.messageResolver
                            .resolveMessage(be, locale);
                    errors.add(new BusinessError(be.getCode(), message, be.getProperty()));
                }
            }
        }
        if (!errors.isEmpty()) {
            try {
                final Map<String, Object> map = new HashMap<>();
                map.put("errors", errors);
                response.getWriter().print(JsonUtil.map2Json(map));
                response.setStatus(SC_BUSINESS_ERROR);
                return new ModelAndView();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 通过显示错误页面的方式处理异常
     *
     * @param request
     *            HTTP请求
     * @param handlerMethod
     *            MVC处理方法
     * @param he
     *            可处理的异常
     * @return 模型视图
     *
     * @author jianglei
     */
    private ModelAndView handleExceptionToPage(final HttpServletRequest request,
            final HandlerMethod handlerMethod, final HandleableException he) {
        final ModelAndView mav = new ModelAndView(HandleableExceptionResult.DEFAULT_VIEW);
        final HandleableExceptionResult ber = handlerMethod
                .getMethodAnnotation(HandleableExceptionResult.class);
        if (ber != null) {
            String view = ber.value();
            if (HandleableExceptionResult.PREV_VIEW.equals(view)) {
                view = WebUtil.getRelativePreviousUrl(request, false);
            }
            mav.setViewName(view);
            mav.addObject("back", ber.back());
            if (ber.handler()) { // 自定义处理器
                final Object controller = handlerMethod.getBean();
                if (controller instanceof HandleableExceptionHandler) {
                    final HandleableExceptionHandler exceptionHandler = (HandleableExceptionHandler) controller;
                    final Method method = handlerMethod.getMethod();
                    exceptionHandler.handleException(method.getName(), he, mav);
                }
            }
            // 不论默认处理方式还是自定义处理，均从@HandleableExceptionResult注解中获取异常处理完毕后要生成校验规则的模型类集合
            this.handlerValidationGenerator.generate(request, ber.validate(), mav);
        }
        logException(he, request);
        request.setAttribute(ErrorTagSupport.EXCEPTION_KEY, he);
        return mav;
    }

    @Override
    protected void logException(final Exception ex, final HttpServletRequest request) {
        if (ex instanceof BusinessException) {
            logException((BusinessException) ex);
        } else if (ex instanceof MultiException) {
            final MultiException me = (MultiException) ex;
            for (final SingleException se : me) {
                if (se instanceof BusinessException) {
                    logException((BusinessException) se);
                }
            }
        } else {
            super.logException(ex, request);
        }
    }

    /**
     * 打印业务异常
     *
     * @param be
     *            业务异常
     */
    private void logException(final BusinessException be) {
        if (this.logger.isErrorEnabled()) {
            final StringBuffer message = new StringBuffer(be.getCode());
            final String args = StringUtils.join(be.getArgs(), Strings.COMMA);
            if (args.length() > 0) {
                message.append(Strings.COLON).append(args);
            }
            if (be.isBoundProperty()) {
                message.append(Strings.LEFT_BRACKET).append(be.getProperty())
                        .append(Strings.RIGHT_BRACKET);
            }
            this.logger.error(message);
        }
    }
}
