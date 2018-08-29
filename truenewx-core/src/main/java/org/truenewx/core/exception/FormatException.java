package org.truenewx.core.exception;

import org.springframework.util.Assert;
import org.truenewx.core.util.ClassUtil;

/**
 * 格式异常，必须绑定属性
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FormatException extends SingleException {

    private static final long serialVersionUID = -7599751978935457915L;

    private Class<?> beanClass;
    private String violationMessage;

    public FormatException(Class<?> beanClass, String property, String violationMessage) {
        super(violationMessage);
        this.beanClass = beanClass;
        Assert.notNull(property, "property must be not null");
        this.property = property;
    }

    public FormatException(String property, String violationMessage) {
        this(null, property, violationMessage);
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public String getViolationMessage() {
        return this.violationMessage;
    }

    @Override
    public boolean matches(String property) {
        if (super.matches(property)) {
            return true;
        }
        String simplePropertyPath = ClassUtil.getSimplePropertyPath(this.beanClass, property);
        if (simplePropertyPath.equals(property)) {
            return true;
        }
        String fullPropertyPath = ClassUtil.getFullPropertyPath(this.beanClass, property);
        return fullPropertyPath.equals(property);
    }
}
