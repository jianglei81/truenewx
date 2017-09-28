package org.truenewx.core.exception;

import java.util.Objects;

import org.springframework.util.Assert;
import org.truenewx.core.Strings;

/**
 * 格式异常，必须绑定属性
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FormatException extends SingleException {

    private static final long serialVersionUID = -3765826642930580588L;

    private Class<?> beanClass;
    private String violationMessage;

    public FormatException(final Class<?> beanClass, final String property,
            final String violationMessage) {
        super(violationMessage);
        this.beanClass = beanClass;
        Assert.notNull(property, "property must be not null");
        this.property = property;
    }

    public FormatException(final String property, final String violationMessage) {
        this(null, property, violationMessage);
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public String getViolationMessage() {
        return this.violationMessage;
    }

    public String getFullPropertyPath() {
        final StringBuffer path = new StringBuffer();
        if (this.beanClass != null) {
            path.append(this.beanClass.getName()).append(Strings.WELL);
        }
        path.append(this.property);
        return path.toString();
    }

    public String getSimplePropertyPath() {
        final StringBuffer path = new StringBuffer();
        if (this.beanClass != null) {
            path.append(this.beanClass.getSimpleName()).append(Strings.WELL);
        }
        path.append(this.property);
        return path.toString();
    }

    public boolean matches(final Class<?> beanClass, final String property) {
        return Objects.equals(this.beanClass, beanClass) && this.property.equals(property);
    }

    @Override
    public boolean matches(final String property) {
        if (this.property == null) { // 未绑定属性，则指定匹配空属性
            return property == null;
        } else { // 已绑定属性，则*、属性、简单路径、完全路径匹配一个即可
            return Strings.ASTERISK.equals(property) || this.property.equals(property)
                    || getSimplePropertyPath().equals(property)
                    || getFullPropertyPath().equals(property);
        }
    }
}
