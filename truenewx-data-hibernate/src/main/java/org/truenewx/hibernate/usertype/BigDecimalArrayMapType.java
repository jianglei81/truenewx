package org.truenewx.hibernate.usertype;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.truenewx.core.Strings;
import org.truenewx.core.util.MathUtil;
import org.truenewx.core.util.StringUtil;

/**
 * 原始浮点型数组映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class BigDecimalArrayMapType extends ArrayMapType {

    @Override
    public Class<?> returnedClass() {
        return BigDecimal[].class;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
            final SharedSessionContractImplementor session, final Object owner)
            throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (value != null) {
            final String[] array = value.split(Strings.COMMA);
            final BigDecimal[] result = new BigDecimal[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = MathUtil.parseDecimal(array[i]);
            }
            return result;
        }
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
            final SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value != null) {
            final Object[] array = (Object[]) value;
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
        final BigDecimal[] array = (BigDecimal[]) value;
        final BigDecimal[] result = new BigDecimal[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

}
