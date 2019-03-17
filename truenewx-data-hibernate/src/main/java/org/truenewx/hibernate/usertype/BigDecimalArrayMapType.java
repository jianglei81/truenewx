package org.truenewx.hibernate.usertype;

import java.math.BigDecimal;
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
    public Object nullSafeGet(ResultSet rs, String[] names,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if (value != null) {
            String[] array = StringUtils.split(value, (Strings.COMMA));
            BigDecimal[] result = new BigDecimal[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = MathUtil.parseDecimal(array[i]);
            }
            return result;
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
            SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            Object[] array = (Object[]) value;
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
        BigDecimal[] array = (BigDecimal[]) value;
        BigDecimal[] result = new BigDecimal[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

}
