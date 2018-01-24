package org.truenewx.web.enums.tag;

import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTag;
import javax.servlet.jsp.tagext.IterationTag;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.core.ForEachSupport;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.enums.support.EnumDictResolver;
import org.truenewx.core.enums.support.EnumType;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.spring.util.SpringWebUtil;

/**
 * 枚举类型遍历标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumForEachTag extends ForEachSupport implements LoopTag, IterationTag {

    private static final long serialVersionUID = -798971033318943907L;

    private String type;
    private String subtype;

    public final void setType(final String type) throws JspException {
        this.type = getElExpressionValue("type", type, String.class);
    }

    public final void setSubtype(final String subtype) throws JspException {
        this.subtype = getElExpressionValue("subtype", this.type, String.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T getElExpressionValue(final String attributeName, final String expression,
            final Class<T> expectedType) throws JspException {
        return (T) ExpressionEvaluatorManager.evaluate(attributeName, expression, expectedType,
                this.pageContext);
    }

    @Override
    protected void prepare() throws JspTagException {
        final EnumDictResolver enumDictResolver = getBeanFromApplicationContext(
                EnumDictResolver.class);
        final EnumType enumType = enumDictResolver.getEnumType(this.type, this.subtype,
                getLocale());
        if (enumType != null) {
            this.rawItems = enumType.getItems();
            super.prepare();
        }
    }

    private <T> T getBeanFromApplicationContext(final Class<T> beanClass) {
        final ApplicationContext context = SpringWebUtil.getApplicationContext(this.pageContext);
        if (context != null) {
            return SpringUtil.getFirstBeanByClass(context, beanClass);
        }
        return null;
    }

    private Locale getLocale() {
        return this.pageContext.getRequest().getLocale();
    }

}
