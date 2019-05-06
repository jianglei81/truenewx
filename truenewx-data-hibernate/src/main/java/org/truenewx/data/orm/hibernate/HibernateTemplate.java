package org.truenewx.data.orm.hibernate;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.TypeHelper;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.Query;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.enums.annotation.EnumValue;
import org.truenewx.data.orm.DataAccessTemplate;
import org.truenewx.hibernate.usertype.EnumValueMapType;

import com.google.common.base.Enums;

/**
 * Hibernate数据访问模板
 *
 * @author jianglei
 * @since JDK 1.8
 */
public final class HibernateTemplate extends DataAccessTemplate {

    private SessionFactory sessionFactory;
    private boolean sqlMode;

    @Autowired(required = false)
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public HibernateTemplate toSqlMode() {
        HibernateTemplate ht = new HibernateTemplate();
        ht.sessionFactory = this.sessionFactory;
        ht.sqlMode = true;
        return ht;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public Session getSession() {
        return getSessionFactory().getCurrentSession();
    }

    public Dialect getDialect() {
        return ((SessionFactoryImplementor) getSessionFactory()).getJdbcServices().getDialect();
    }

    @SuppressWarnings("unchecked")
    private <T> Query<T> createQuery(CharSequence hql) {
        Session session = getSession();
        if (this.sqlMode) {
            return session.createNativeQuery(hql.toString());
        }
        return session.createQuery(hql.toString());
    }

    @Override
    public <T> List<T> list(CharSequence hql, String paramName, Object paramValue, int pageSize,
            int pageNo) {
        Query<T> query = createQuery(hql);
        applyParamToQuery(query, paramName, paramValue);
        applyPagingToQuery(query, pageSize, pageNo, false);
        return query.list();
    }

    @Override
    public <T> List<T> list(CharSequence hql, Map<String, ?> params, int pageSize, int pageNo) {
        Query<T> query = createQuery(hql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, false);
        return query.list();
    }

    @Override
    public <T> List<T> list(CharSequence hql, List<?> params, int pageSize, int pageNo) {
        Query<T> query = createQuery(hql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, false);
        return query.list();
    }

    @Override
    public <T> List<T> listWithOneMore(CharSequence hql, String paramName, Object paramValue,
            int pageSize, int pageNo) {
        Query<T> query = createQuery(hql);
        applyParamToQuery(query, paramName, paramValue);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.list();
    }

    @Override
    public <T> List<T> listWithOneMore(CharSequence hql, Map<String, ?> params, int pageSize,
            int pageNo) {
        Query<T> query = createQuery(hql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.list();
    }

    @Override
    public <T> List<T> listWithOneMore(CharSequence hql, List<?> params, int pageSize, int pageNo) {
        Query<T> query = createQuery(hql);
        applyParamsToQuery(query, params);
        applyPagingToQuery(query, pageSize, pageNo, true);
        return query.list();
    }

    @Override
    public int update(CharSequence hql, String paramName, Object paramValue) {
        Query<?> query = createQuery(hql);
        applyParamToQuery(query, paramName, paramValue);
        return query.executeUpdate();
    }

    @Override
    public int update(CharSequence hql, Map<String, ?> params) {
        Query<?> query = createQuery(hql);
        applyParamsToQuery(query, params);
        return query.executeUpdate();
    }

    @Override
    public int update(CharSequence hql, List<?> params) {
        Query<?> query = createQuery(hql);
        applyParamsToQuery(query, params);
        return query.executeUpdate();
    }

    public void applyParamsToQuery(Query<?> query, Map<String, ?> params) {
        if (params != null) {
            for (Entry<String, ?> entry : params.entrySet()) {
                applyParamToQuery(query, entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 将指定参数名和参数值写入指定Hibernate查询对象中
     *
     * @param query Hibernate查询对象
     * @param name  参数名
     * @param value 参数值，除常见类型外，还支持Collection、数组、枚举
     */
    public void applyParamToQuery(Query<?> query, String name, Object value) {
        if (value instanceof Collection) {
            query.setParameterList(name, (Collection<?>) value);
        } else if (value instanceof Object[]) { // 对象数组
            query.setParameterList(name, (Object[]) value);
        } else if (value != null) {
            Class<?> clazz = value.getClass();
            if (clazz.isArray()) { // 基础数据数组
                Collection<Object> collection = new ArrayList<>();
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    collection.add(Array.get(value, i));
                }
                query.setParameterList(name, collection);
            } else if (clazz.isEnum()) {
                Enum<?> enumConstant = (Enum<?>) value;
                Field field = Enums.getField(enumConstant);
                EnumValue ev = field.getAnnotation(EnumValue.class);
                if (ev != null) { // 含有@EnumValue注解的枚举参数值，需通过自定义类型转换
                    Properties parameters = new Properties();
                    parameters.put(EnumValueMapType.PARAMETER_CLASS, clazz.getName());
                    query.setParameter(name, value, customType(EnumValueMapType.class, parameters));
                }
            } else {
                query.setParameter(name, value);
            }
        } else {
            query.setParameter(name, value);
        }
    }

    public void applyParamsToQuery(Query<?> query, List<?> params) {
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                applyParamToQuery(query, i, params.get(i));
            }
        }
    }

    public void applyParamToQuery(Query<?> query, int position, Object value) {
        if (value != null) {
            Class<?> clazz = value.getClass();
            if (clazz.isEnum()) {
                Enum<?> enumConstant = (Enum<?>) value;
                Field field = Enums.getField(enumConstant);
                EnumValue ev = field.getAnnotation(EnumValue.class);
                if (ev != null) { // 含有@EnumValue注解的枚举参数值，需通过自定义类型转换
                    Properties parameters = new Properties();
                    parameters.put(EnumValueMapType.PARAMETER_CLASS, clazz.getName());
                    query.setParameter(position, value,
                            customType(EnumValueMapType.class, parameters));
                }
            } else {
                query.setParameter(position, value);
            }
        } else {
            query.setParameter(position, value);
        }
    }

    public void applyPagingToQuery(Query<?> query, int pageSize, int pageNo, boolean oneMore) {
        if (pageSize > 0) { // 用页大小判断是否分页查询
            if (pageNo <= 0) { // 页码最小为1
                pageNo = 1;
            }
            query.setFirstResult(pageSize * (pageNo - 1));
            query.setMaxResults(oneMore ? (pageSize + 1) : pageSize);
        }
    }

    /**
     * 获取Hibernate自定义映射类型<br/>
     * 详见：{@link org.hibernate.TypeHelper#custom(Class, Properties)}
     */
    public Type customType(Class<?> userTypeClass, Properties properties) {
        TypeHelper typeHelper = getSessionFactory().getTypeHelper();
        return typeHelper.custom(userTypeClass, properties);
    }
}
