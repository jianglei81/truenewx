package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
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
    public Object nullSafeGet(final ResultSet rs, final String[] names,
                    final SessionImplementor session, final Object owner)
                    throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (value != null) {
            return value.split(Strings.COMMA);
        }
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
                    final SessionImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            final String[] array = (String[]) value;
            if (this.size > 0 && array.length > this.size) {
                throw getSizeException();
            }
            st.setString(index, StringUtils.join(array, Strings.COMMA));
        } else {
            st.setString(index, null);
        }
    }

}
