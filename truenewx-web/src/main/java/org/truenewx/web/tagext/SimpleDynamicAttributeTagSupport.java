package org.truenewx.web.tagext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.spring.util.SpringWebUtil;

/**
 * 简单动态属性标签支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SimpleDynamicAttributeTagSupport extends SimpleTagSupport
        implements DynamicAttributes {
    /**
     * 属性名-值映射集
     */
    protected Map<String, Object> attributes = new HashMap<>();

    @Override
    public final void setDynamicAttribute(final String uri, final String localName,
            final Object value) throws JspException {
        if (value != null) {
            this.attributes.put(localName, value);
        }
    }

    /**
     * 将属性拼成属性串
     *
     * @param ignoredAttributes
     *            忽略的属性
     * @return 属性串
     */
    protected String joinAttributes(final String... ignoredAttributes) {
        final StringBuffer sb = new StringBuffer();
        for (final Entry<String, Object> entry : this.attributes.entrySet()) {
            final String name = entry.getKey();
            if (!ArrayUtils.contains(ignoredAttributes, name)) {
                sb.append(Strings.SPACE).append(name).append(Strings.EQUAL)
                        .append(Strings.DOUBLE_QUOTES).append(entry.getValue())
                        .append(Strings.DOUBLE_QUOTES);
            }
        }
        return sb.toString();
    }

    protected final PageContext getPageContext() {
        return (PageContext) getJspContext();
    }

    protected final HttpServletRequest getRequest() {
        return (HttpServletRequest) getPageContext().getRequest();
    }

    protected final Locale getLocale() {
        return getPageContext().getRequest().getLocale();
    }

    /**
     * 从Spring上下文容器中获取指定类型的bean对象
     *
     * @param beanClass
     *            bean类型
     * @return bean对象
     */
    protected final <T> T getBeanFromApplicationContext(final Class<T> beanClass) {
        final ApplicationContext context = SpringWebUtil.getApplicationContext(getPageContext());
        if (context != null) {
            return SpringUtil.getFirstBeanByClass(context, beanClass);
        }
        return null;
    }

    /**
     * 获取EL表达式的值
     *
     * @param attributeName
     *            属性名
     * @param expression
     *            表达式
     * @param expectedType
     *            期望值的类型
     * @return EL表达式值
     * @throws JspException
     *             如果EL表达式错误
     */
    @SuppressWarnings("unchecked")
    protected final <T> T getElExpressionValue(final String attributeName, final String expression,
            final Class<T> expectedType) throws JspException {
        return (T) ExpressionEvaluatorManager.evaluate(attributeName, expression, expectedType,
                getPageContext());
    }

    /**
     * 向响应中打印指定值集
     *
     * @param values
     *            值集
     * @throws IOException
     *             如果出现输出错误
     */
    protected final void print(final Object... values) throws IOException {
        if (values != null) {
            final JspWriter writer = getJspContext().getOut();
            for (final Object value : values) {
                writer.print(value);
            }
        }
    }

}
