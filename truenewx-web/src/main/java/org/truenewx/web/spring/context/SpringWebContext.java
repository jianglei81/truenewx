package org.truenewx.web.spring.context;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.truenewx.web.spring.util.SpringWebUtil;
import org.truenewx.web.util.WebUtil;

/**
 * Spring Web上下文工具类<br/>
 * 要求web.xml中具有如下配置：<br/>
 * &lt;listener&gt;<br/>
 * &lt;listener-class&gt;<br/>
 * org.springframework.web.context.request.RequestContextListener<br/>
 * &lt;/listener-class&gt;<br/>
 * &lt;/listener&gt;
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SpringWebContext {

    private SpringWebContext() {
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        return sra.getRequest();
    }

    public static HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletWebRequest) {
            return ((ServletWebRequest) requestAttributes).getResponse();
        }
        return null;
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletContext getServletContext() {
        return getSession().getServletContext();
    }

    /**
     * @return 区域
     */
    public static Locale getLocale() {
        return SpringWebUtil.getLocale(getRequest());
    }

    /**
     * 设置request的属性
     *
     * @param name  属性名
     * @param value 属性值
     */
    public static void set(String name, Object value) {
        getRequest().setAttribute(name, value);
    }

    /**
     * 获取request的属性值
     *
     * @param name 属性名
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String name) {
        return (T) getRequest().getAttribute(name);
    }

    /**
     * 移除request的属性
     *
     * @param name 属性名
     * @return 被移除的属性值，没有该属性则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T remove(String name) {
        HttpServletRequest request = getRequest();
        Object value = request.getAttribute(name);
        if (value != null) {
            request.removeAttribute(name);
        }
        return (T) value;
    }

    /**
     * 设置属性至SESSION中
     *
     * @param name  属性名
     * @param value 属性值
     */
    public static void setToSession(String name, Object value) {
        getSession().setAttribute(name, value);
    }

    /**
     * 从SESSION获取指定属性
     *
     * @param name 属性
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFromSession(String name) {
        return (T) getSession().getAttribute(name);
    }

    /**
     * 移除SESSION中的指定属性
     *
     * @param name 属性名
     * @return 被移除的属性值，没有该属性则返回null
     */
    public static <T> T removeFromSession(String name) {
        HttpSession session = getSession();
        return WebUtil.removeAttribute(session, name);
    }

    /**
     * 转换指定结果名为直接重定向的结果名
     *
     * @param result 结果名
     * @return 直接重定向的结果名
     */
    public static String toRedirectResult(String result) {
        return StringUtils.join("redirect:", result);
    }

    public static RequestMethod getRequestMethod() {
        String method = getRequest().getMethod().toUpperCase();
        return EnumUtils.getEnum(RequestMethod.class, method);
    }

    /**
     * 使当前session失效，下次再使用session时将重新创建新的session
     */
    public static void invalidateSession() {
        getSession().invalidate();
    }
}
