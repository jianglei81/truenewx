package org.truenewx.hibernate.usertype;

import java.lang.reflect.Array;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.truenewx.core.util.MathUtil;

/**
 * 数组映射类型
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class ArrayMapType extends AbstractUserType implements ParameterizedType {
    /**
     * 数组长度限制
     */
    protected int size;

    @Override
    public void setParameterValues(final Properties parameters) {
        if (parameters != null) {
            final String size = parameters.getProperty("size");
            if (size != null) {
                this.size = MathUtil.parseInt(size);
            }
        }
    }

    public static HibernateException getSizeException(final int size) {
        return new HibernateException("Array's length cannot greater than " + size);
    }

    protected HibernateException getSizeException() {
        return getSizeException(this.size);
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        final int length = ((Object[]) value).length;
        final Object result = Array.newInstance(returnedClass().getComponentType(), length);
        System.arraycopy(value, 0, result, 0, length);
        return result;
    }
}
