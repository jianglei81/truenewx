package org.truenewx.hibernate.usertype;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.truenewx.core.Strings;
import org.truenewx.core.util.ArrayUtil;
import org.truenewx.core.util.JsonUtil;

/**
 * 对象元素集合映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class ObjectComponentMapType extends AbstractUserType implements ParameterizedType {

    protected Class<?> componentType;
    private String[] excludeProperties;

    @Override
    public void setParameterValues(final Properties parameters) {
        if (parameters != null) {
            final String className = parameters.getProperty("componentType");
            if (StringUtils.isNotBlank(className)) {
                this.componentType = classForName(className);
                final String exclude = parameters.getProperty("excluded");
                if (StringUtils.isNotBlank(exclude)) {
                    this.excludeProperties = exclude.split(Strings.COMMA);
                    ArrayUtil.trim(this.excludeProperties);
                }
            }
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
            final SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        String json = null;
        if (value != null) {
            if (this.componentType != null && ArrayUtils.isNotEmpty(this.excludeProperties)) {
                json = JsonUtil.toJson(value, this.componentType, this.excludeProperties);
            } else {
                json = JsonUtil.toJson(value);
            }
        }
        st.setString(index, json);
    }

}
