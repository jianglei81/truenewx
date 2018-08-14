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

/**
 * Spring的业务异常解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class BusinessExceptionResolver extends AbstractHandlerExceptionResolver {
    /**
     * 业务异常消息解析器
     */
    private BusinessExceptionMessageResolver messageResolver;
    /**
     * 处理器校验生成器
     */
    private HandlerValidationGenerator handlerValidationGenerator;

    @Autowired
    public void setMessageResolver(BusinessExceptionMessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @Autowired
    public void setHandlerValidationGenerator(
            HandlerValidationGenerator handlerValidationGenerator) {
        this.handlerValidationGenerator = handlerValidationGenerator;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception e) {
        ModelAndView mav = null;
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            HandleableExceptionMessage hem = handlerMethod
                    .getMethodAnnotation(HandleableExceptionMessage.class);
            if (hem != null) {
                mav = handleExceptionToMessage(e, request.getLocale(), response,
                        hem.respondErrorStatus());
            } else if (e instanceof HandleableException) {
                mav = handleExceptionToPage(request, handlerMethod, (HandleableException) e);
            } else {
                LoggerFactory.getLogger(handlerMethod.getBeanType()).error(e.getMessage(), e);
            }
        }
        return mav;
    }

    /**
     * 以错误消息的方式处理异常
     *
     * @param e
     *            异常
     * @param response
     *            HTTP响应
     * @param respondErrorStatus
     *            是否返回表示业务异常的Response错误状态码
     * @return 模型视图
     */
    private ModelAndView handleExceptionToMessage(Exception e, Locale locale,
            HttpServletResponse response, boolean respondErrorStatus) {
        List<HandledError> errors = new ArrayList<>();
        if (e instanceof BusinessException) { // 业务异常，转换错误消息
            BusinessException be = (BusinessException) e;
            String message = this.messageResolver.resolveMessage(be, locale);
            errors.add(new HandledError(be.getCode(), message, be.getProperty()));
        } else if (e instanceof MultiException) { // 业务异常集，转换错误消息
            MultiException me = (MultiException) e;
            for (SingleException se : me) {
                if (se instanceof BusinessException) {
                    BusinessException be = (BusinessException) se;
                    String message = BusinessExceptionResolver.this.messageResolver
                            .resolveMessage(be, locale);
                    errors.add(new HandledError(be.getCode(), message, be.getProperty()));
                }
            }
        }
        if (!errors.isEmpty()) {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("errors", errors);
                response.getWriter().print(JsonUtil.toJson(map));
                if (respondErrorStatus) {
                    response.setStatus(HandledError.SC_HANDLED_ERROR);
                }
                return new ModelAndView();
            } catch (IOException ex) {
                this.logger.error(e.getMessage(), e);
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
    private ModelAndView handleExceptionToPage(HttpServletRequest request,
            HandlerMethod handlerMethod, HandleableException he) {
        ModelAndView mav = new ModelAndView(HandleableExceptionResult.DEFAULT_VIEW);
        mav.addObject("ajaxRequest", WebUtil.isAjaxRequest(request));
        HandleableExceptionResult her = handlerMethod
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
                Object controller = handlerMethod.getBean();
                if (controller instanceof HandleableExceptionHandler) {
                    HandleableExceptionHandler exceptionHandler = (HandleableExceptionHandler) controller;
                    Method method = handlerMethod.getMethod();
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
    protected void logException(Exception ex, HttpServletRequest request) {
        if (ex instanceof BusinessException) {
            logException((BusinessException) ex);
        } else if (ex instanceof MultiException) {
            MultiException me = (MultiException) ex;
            for (SingleException se : me) {
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
    private void logException(BusinessException e) {
        if (this.logger.isErrorEnabled()) {
            StringBuffer message = new StringBuffer(e.getCode());
            String args = StringUtils.join(e.getArgs(), Strings.COMMA);
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
