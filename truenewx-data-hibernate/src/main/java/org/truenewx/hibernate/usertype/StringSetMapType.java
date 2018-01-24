package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.truenewx.core.Strings;
import org.truenewx.core.functor.impl.PredEqual;

/**
 * 字符串集合映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class StringSetMapType extends AbstractUserType {

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return Set.class;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return PredEqual.INSTANCE.apply(x, y);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object deepCopy(final Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        return new LinkedHashSet<>((Set<Enum<?>>) value);
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
        final SharedSessionContractImplementor session, final Object owner)
        throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (value != null) {
            final String[] array = value.split(Strings.COMMA);
            final Set<String> result = new LinkedHashSet<>();
            for (final String s : array) {
                result.add(s);
            }
            return result;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
        final SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            final Set<String> set = (Set<String>) value;
            st.setString(index, StringUtils.join(set, Strings.COMMA));
        } else {
            st.setObject(index, null);
        }
    }

}
