package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.truenewx.core.util.JsonUtil;

/**
 * 
 * Map-Json映射类型
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class MapJsonMapType extends AbstractUserType {

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return Map.class;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
                    final SessionImplementor session, final Object owner)
                    throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (StringUtils.isNotBlank(value)) {
            try {
                return JsonUtil.json2Map(value);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
                    final SessionImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            final Map<String, Object> map = (Map<String, Object>) value;
            st.setString(index, JsonUtil.map2Json(map));
        } else {
            st.setString(index, null);
        }
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        return new LinkedHashMap<>((Map<?, ?>) value);
    }

}
