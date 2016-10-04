package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.truenewx.core.Strings;
import org.truenewx.core.util.MathUtil;
import org.truenewx.core.util.StringUtil;

/**
 * 原始整型数组映射类型
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class IntArrayMapType extends ArrayMapType {

    @Override
    public Class<?> returnedClass() {
        return int[].class;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
                    final SessionImplementor session, final Object owner)
                    throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (value != null) {
            final String[] array = value.split(Strings.COMMA);
            final int[] result = new int[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = MathUtil.parseInt(array[i]);
            }
            return result;
        }
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
                    final SessionImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            final int[] array = (int[]) value;
            if (this.size > 0 && array.length > this.size) {
                throw getSizeException();
            }
            st.setString(index, StringUtil.join(Strings.COMMA, array));
        } else {
            st.setString(index, null);
        }
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        final int[] array = (int[]) value;
        final int[] result = new int[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

}
