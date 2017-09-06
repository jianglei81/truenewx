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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
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
public class BusinessExceptionResolver extends AbstractHandlerExceptionResolver {
    /**
     * 响应状态码：业务错误
     */
    public static final int SC_BUSINESS_ERROR = HttpServletResponse.SC_CONFLICT; // 对当前资源状态，请求不能完成

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
            } else {
                final Logger handlerLogger = LoggerFactory.getLogger(handlerMethod.getBeanType());
                if (handlerLogger.isErrorEnabled()) {
                    handlerLogger.error(e.getMessage(), e);
                }
            }
            final ValidationGeneratable vg = handlerMethod
                    .getMethodAnnotation(ValidationGeneratable.class);
            if (vg != null) {
                this.handlerValidationGenerator.generate(request, vg.value(), mav);
            }
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
                response.getWriter().print(JsonUtil.toJson(map));
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
        final HandleableExceptionResult her = handlerMethod
                .getMethodAnnotation(HandleableExceptionResult.class);
        if (her != null) {
            String view = her.value();
            if (HandleableExceptionResult.PREV_VIEW.equals(view)) {
                view = WebUtil.getRelativePreviousUrl(request, false);
            }
            if (HandleableExceptionResult.DEFAULT_VIEW.equals(view)) { // 跳转到全局错误页面，则需设置返回按钮地址
                mav.addObject("back", her.back());
            } else { // 非跳转到全局错误页面，则复制参数到属性集中，以便于可能的回填
                mav.setViewName(view);
                WebUtil.copyParameters2Attributes(request);
            }
            if (her.handler()) { // 自定义处理器
                final Object controller = handlerMethod.getBean();
                if (controller instanceof HandleableExceptionHandler) {
                    final HandleableExceptionHandler exceptionHandler = (HandleableExceptionHandler) controller;
                    final Method method = handlerMethod.getMethod();
                    exceptionHandler.handleException(method.getName(), he, mav);
                }
            }
            // 不论默认处理方式还是自定义处理，均从@HandleableExceptionResult注解中获取异常处理完毕后要生成校验规则的模型类集合
            this.handlerValidationGenerator.generate(request, her.validate(), mav);
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
     * @param e
     *            业务异常
     */
    private void logException(final BusinessException e) {
        if (this.logger.isErrorEnabled()) {
            final StringBuffer message = new StringBuffer(e.getCode());
            final String args = StringUtils.join(e.getArgs(), Strings.COMMA);
            if (args.length() > 0) {
                message.append(Strings.COLON).append(args);
            }
            if (e.isBoundProperty()) {
                message.append(Strings.LEFT_BRACKET).append(e.getProperty())
                        .append(Strings.RIGHT_BRACKET);
            }
            this.logger.error(message);
        }
    }
}
