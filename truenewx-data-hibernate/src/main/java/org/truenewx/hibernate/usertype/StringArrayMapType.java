package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.truenewx.core.Strings;

/**
 * 字符串数组映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class StringArrayMapType extends ArrayMapType {

    @Override
    public Class<?> returnedClass() {
        return String[].class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if (StringUtils.isNotBlank(value)) {
            return value.split(Strings.COMMA);
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
            SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value instanceof String[]) {
            String[] array = (String[]) value;
            if (this.size > 0 && array.length > this.size) {
                throw getSizeException();
            }
            String s = StringUtils.join(array, Strings.COMMA);
            if (StringUtils.isBlank(s)) {
                s = null;
            }
            st.setString(index, s);
        } else {
            st.setString(index, null);
        }
    }

}
