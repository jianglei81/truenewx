package org.truenewx.hibernate.usertype;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.truenewx.core.Strings;
import org.truenewx.core.enums.support.functor.AlgoEnumValueOf;
import org.truenewx.core.enums.support.functor.FuncEnumValue;
import org.truenewx.core.functor.algorithm.impl.AlgoJoin;
import org.truenewx.core.functor.impl.PredEqual;
import org.truenewx.core.util.MathUtil;

/**
 * 枚举数组映射类
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumArrayMapType extends EnumValueMapType {
    /**
     * 数组长度限制
     */
    private int size;

    @Override
    public void setParameterValues(final Properties parameters) {
        super.setParameterValues(parameters);
        final String size = parameters.getProperty("size");
        if (size != null) {
            this.size = MathUtil.parseInt(size);
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return Object[].class;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return PredEqual.INSTANCE.apply(x, y);
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        final Enum<?>[] array = (Enum<?>[]) value;
        final Enum<?>[] result = new Enum<?>[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
                    final SessionImplementor session, final Object owner)
                    throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (value != null) {
            final String[] array = value.split(Strings.COMMA);
            final Class<Enum<?>> enumClass = getEnumClass(owner);
            final Object[] result = (Object[]) Array.newInstance(enumClass, array.length);
            for (int i = 0; i < array.length; i++) {
                result[i] = AlgoEnumValueOf.visit(enumClass, array[i]);
                if (result[i] == null) {
                    throw new NullPointerException();
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
                    final SessionImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            final Enum<?>[] array = (Enum<?>[]) value;
            if (this.size > 0 && array.length > this.size) {
                throw ArrayMapType.getSizeException(this.size);
            }
            st.setString(index, AlgoJoin.visit(array, Strings.COMMA, FuncEnumValue.INSTANCE));
        } else {
            st.setObject(index, null);
        }
    }

}
