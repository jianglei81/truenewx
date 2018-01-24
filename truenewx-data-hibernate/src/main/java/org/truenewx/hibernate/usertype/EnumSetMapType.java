package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.truenewx.core.Strings;
import org.truenewx.core.enums.support.functor.AlgoEnumValueOf;
import org.truenewx.core.enums.support.functor.FuncEnumValue;
import org.truenewx.core.functor.algorithm.impl.AlgoJoin;
import org.truenewx.core.functor.impl.PredEqual;

/**
 * 枚举集合映射类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumSetMapType extends EnumValueMapType {

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
            final Class<Enum<?>> enumClass = getEnumClass(owner);
            final Set<Enum<?>> result = new LinkedHashSet<>();
            for (final String s : array) {
                final Enum<?> contant = AlgoEnumValueOf.visit(enumClass, s);
                if (contant != null) {
                    result.add(contant);
                }
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
            final Set<Enum<?>> set = (Set<Enum<?>>) value;
            st.setString(index, AlgoJoin.visit(set, Strings.COMMA, FuncEnumValue.INSTANCE));
        } else {
            st.setObject(index, null);
        }
    }

}
