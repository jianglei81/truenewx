package org.truenewx.core.exception;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.functor.impl.FuncHashCode;
import org.truenewx.core.functor.impl.PredEqual;

/**
 * 业务异常，可以绑定属性，默认未绑定属性
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class BusinessException extends SingleException {

    private static final long serialVersionUID = 3188183601455385859L;

    private String code;
    private Object[] args;

    public BusinessException(final String code, final Object... args) {
        super(code);
        this.code = code;
        this.args = args == null ? new Object[0] : args;
    }

    public String getCode() {
        return this.code;
    }

    public Object[] getArgs() {
        return this.args;
    }

    /**
     * 与指定属性绑定
     *
     * @param property 绑定的属性
     * @return 当前异常对象自身
     */
    public BusinessException bind(final String property) {
        this.property = property;
        return this;
    }

    /**
     * 判断是否已绑定属性
     *
     * @return 是否已绑定属性
     */
    public boolean isBoundProperty() {
        return StringUtils.isNotBlank(this.property);
    }

    @Override
    public int hashCode() {
        Object[] array = { this.code, this.args };
        return FuncHashCode.INSTANCE.apply(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BusinessException other = (BusinessException) obj;
        return PredEqual.INSTANCE.apply(this.code, other.code)
                && PredEqual.INSTANCE.apply(this.args, other.args);
    }

}
