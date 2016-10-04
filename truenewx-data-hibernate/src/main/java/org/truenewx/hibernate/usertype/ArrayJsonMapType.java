package org.truenewx.hibernate.usertype;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.truenewx.core.util.JsonUtil;

/**
 * 对象数组-JSON字符串映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ArrayJsonMapType extends ObjectComponentMapType {

    @Override
    public Class<?> returnedClass() {
        return Object[].class;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
                    final SessionImplementor session, final Object owner)
                    throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (StringUtils.isNotBlank(value)) {
            if ("[]".equals(value)) {
                if (this.componentType == null) {
                    return new Object[0];
                } else {
                    return Array.newInstance(this.componentType, 0);
                }
            }
            try {
                if (this.componentType == null) {
                    return JsonUtil.json2Array(value);
                } else {
                    return JsonUtil.json2Array(value, this.componentType);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
