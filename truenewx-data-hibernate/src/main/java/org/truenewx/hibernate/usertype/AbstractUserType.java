package org.truenewx.hibernate.usertype;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.annotations.common.util.ReflectHelper;
import org.hibernate.usertype.UserType;
import org.truenewx.core.functor.impl.PredEqual;

/**
 * 抽象的自定义映射类型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractUserType implements UserType {

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return PredEqual.INSTANCE.apply(x, y);
    }

    @Override
    public int hashCode(final Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner)
                    throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner)
                    throws HibernateException {
        return original;
    }

    protected Class<?> classForName(final String className) {
        try {
            return ReflectHelper.classForName(className);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
