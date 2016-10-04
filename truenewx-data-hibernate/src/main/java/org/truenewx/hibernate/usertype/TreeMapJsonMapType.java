package org.truenewx.hibernate.usertype;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

/**
 * TreeMap-Json映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TreeMapJsonMapType extends MapJsonMapType {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object nullSafeGet(final ResultSet rs, final String[] names,
            final SessionImplementor session, final Object owner) throws HibernateException,
            SQLException {
        final Object obj = super.nullSafeGet(rs, names, session, owner);
        if (obj instanceof Map<?, ?>) {
            final TreeMap tm = new TreeMap<>();
            tm.putAll((Map) obj);
            return tm;
        }
        return obj;
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        return new TreeMap<>((Map<?, ?>) value);
    }

}
