package org.truenewx.hibernate.usertype;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.truenewx.core.util.JsonUtil;

/**
 * List-JSON字符串映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ListJsonMapType extends ObjectComponentMapType {

    @Override
    public Class<?> returnedClass() {
        return List.class;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
            final SessionImplementor session, final Object owner)
            throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (StringUtils.isNotBlank(value)) {
            if ("[]".equals(value)) {
                return new ArrayList<>();
            }
            try {
                if (this.componentType == null) {
                    return JsonUtil.json2List(value);
                } else {
                    return JsonUtil.json2List(value, this.componentType);
                }
            } catch (final Exception e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
