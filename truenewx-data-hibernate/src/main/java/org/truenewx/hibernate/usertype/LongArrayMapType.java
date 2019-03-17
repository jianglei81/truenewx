package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.truenewx.core.Strings;
import org.truenewx.core.util.MathUtil;
import org.truenewx.core.util.StringUtil;

/**
 * 原始长整型数组映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class LongArrayMapType extends ArrayMapType {

    @Override
    public Class<?> returnedClass() {
        return long[].class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if (value != null) {
            String[] array = StringUtils.split(value, (Strings.COMMA));
            long[] result = new long[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = MathUtil.parseLong(array[i]);
            }
            return result;
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
            SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            long[] array = (long[]) value;
            if (this.size > 0 && array.length > this.size) {
                throw getSizeException();
            }
            st.setString(index, StringUtil.join(Strings.COMMA, array));
        } else {
            st.setString(index, null);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        long[] array = (long[]) value;
        long[] result = new long[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

}
