package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.truenewx.core.util.JsonUtil;

/**
 * 对象-JSON字符串映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ObjectJsonMapType extends AbstractUserType implements ParameterizedType {

    private Class<?> returnedClass;
    private boolean appendType;

    @Override
    public void setParameterValues(final Properties parameters) {
        this.returnedClass = classForName(parameters.getProperty("class"));
        this.appendType = Boolean.valueOf(parameters.getProperty("appendType"));
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return this.returnedClass;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
            final SharedSessionContractImplementor session, final Object owner)
            throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (StringUtils.isNotBlank(value)) {
            try {
                return JsonUtil.json2Bean(value);
            } catch (final Exception e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
            final SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value != null) {
            Class<?>[] classes = {};
            if (this.appendType) {
                classes = new Class<?>[] { value.getClass() };
            }
            st.setString(index, JsonUtil.toJson(value, classes));
        } else {
            st.setString(index, null);
        }
    }

}
