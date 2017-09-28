package org.truenewx.hibernate.usertype;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.truenewx.core.enums.support.IllegalEnumValueException;
import org.truenewx.core.enums.support.functor.AlgoEnumValueOf;
import org.truenewx.core.enums.support.functor.FuncEnumValue;
import org.truenewx.core.util.ClassUtil;

import com.google.common.base.Preconditions;

/**
 * 枚举值映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EnumValueMapType extends AbstractUserType implements ParameterizedType {
    /**
     * 参数名：类名
     */
    public static final String PARAMETER_CLASS = "class";

    private String propertyName;
    protected Class<Enum<?>> enumClass;

    @Override
    @SuppressWarnings("unchecked")
    public void setParameterValues(final Properties parameters) {
        final String className = parameters.getProperty(PARAMETER_CLASS);
        if (StringUtils.isNotBlank(className)) {
            final Class<?> clazz = classForName(className);
            if (clazz.isEnum()) {
                this.enumClass = (Class<Enum<?>>) clazz;
            }
        }
        if (this.enumClass == null) {
            this.propertyName = parameters.getProperty("property");
            Preconditions.checkNotNull(this.propertyName,
                    "Parameter class or property must be specified");
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.CHAR };
    }

    @Override
    public Class<?> returnedClass() {
        return Enum.class;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return x == y;
    }

    @SuppressWarnings("unchecked")
    protected Class<Enum<?>> getEnumClass(final Object owner) {
        if (this.enumClass == null) {
            final Class<?> ownerClass = owner.getClass();
            final Field field = ClassUtil.findField(ownerClass, this.propertyName);
            if (field == null) {
                throw new IllegalArgumentException(
                        ownerClass.getName() + " has not property: " + this.propertyName);
            }
            Class<?> fieldClass = field.getType();
            if (fieldClass.isArray()) {
                fieldClass = fieldClass.getComponentType();
            }
            if (!fieldClass.isEnum()) {
                throw new IllegalArgumentException("the class of " + ownerClass.getName() + "."
                        + this.propertyName + "(" + fieldClass.getName() + ") is not enum");
            }
            this.enumClass = (Class<Enum<?>>) fieldClass;
        }
        return this.enumClass;
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names,
            final SharedSessionContractImplementor session, final Object owner)
            throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (value != null) {
            final Class<Enum<?>> enumClass = getEnumClass(owner);
            final Object result = AlgoEnumValueOf.visit(enumClass, value);
            if (result == null) {
                throw new IllegalEnumValueException(enumClass, value);
            }
            return result;
        }
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
            final SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value != null) {
            final Enum<?> enumConstant = (Enum<?>) value;
            final String enumValue = FuncEnumValue.INSTANCE.apply(enumConstant);
            if (enumValue == null) {
                throw new NullPointerException();
            }
            st.setString(index, enumValue);
        } else {
            st.setObject(index, null);
        }
    }

}
