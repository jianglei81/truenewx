package org.truenewx.hibernate.usertype;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.truenewx.core.util.JsonUtil;

/**
 * Set-JSON字符串映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SetJsonMapType extends ObjectComponentMapType {

    @Override
    public Class<?> returnedClass() {
        return Set.class;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
            final SessionImplementor session, final Object owner)
            throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (StringUtils.isNotBlank(value)) {
            if ("[]".equals(value)) {
                return new HashSet<>();
            }
            try {
                List<?> list;
                if (this.componentType == null) {
                    list = JsonUtil.json2List(value);
                } else {
                    list = JsonUtil.json2List(value, this.componentType);
                }
                return new HashSet<>(list);
            } catch (final Exception e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
