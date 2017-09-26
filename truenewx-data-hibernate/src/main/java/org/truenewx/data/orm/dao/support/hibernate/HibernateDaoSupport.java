package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.mapping.Column;
import org.truenewx.core.util.MathUtil;
import org.truenewx.data.model.Entity;
import org.truenewx.data.orm.dao.Dao;
import org.truenewx.data.orm.hibernate.HibernateTemplate;
import org.truenewx.data.query.QueryParameter;
import org.truenewx.data.query.QueryResult;

/**
 * Hibernate DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            数据实体类型
 */
public abstract class HibernateDaoSupport<T extends Entity> extends HibernateEntityDaoSupport<T>
        implements Dao<T> {

    protected final HibernateTemplate getHibernateTemplate() {
        return getDataAccessTemplate(getEntityName());
    }

    protected final String getTableName() {
        return getTableName(getEntityName());
    }

    protected final Class<?> getPropertyClass(final String propertyName) {
        return getPropertyClass(getEntityName(), propertyName);
    }

    protected final Column getColumn(final String propertyName) {
        return getColumn(getEntityName(), propertyName);
    }

    protected final Number getNumberPropertyMaxValue(final String propertyName) {
        final Class<?> propertyClass = getPropertyClass(propertyName);
        if (Number.class.isAssignableFrom(propertyClass)) {
            @SuppressWarnings("unchecked")
            final Class<? extends Number> type = (Class<? extends Number>) propertyClass;
            final Column column = getColumn(propertyName);
            final int precision = column.getPrecision();
            final int scale = column.getScale();
            return MathUtil.maxValue(type, precision, scale);
        }
        return null;
    }

    @Override
    public void delete(final T entity) {
        if (entity != null) {
            getHibernateTemplate().getSession().delete(getEntityName(), entity);
        }
    }

    @Override
    public void save(final T entity) {
        if (entity != null) {
            getHibernateTemplate().getSession().saveOrUpdate(getEntityName(), entity);
        }
    }

    @Override
    public final void refresh(final T entity) {
        if (entity != null) {
            getHibernateTemplate().getSession().refresh(getEntityName(), entity);
        }
    }

    @Override
    public final void flush() {
        getHibernateTemplate().getSession().flush();
    }

    /**
     * 获取实体名称
     *
     * @return 实体名称
     */
    protected String getEntityName() {
        return getEntityClass().getName();
    }

    @Override
    public T first() {
        return first(getEntityName());
    }

    @Override
    public List<T> find(final Map<String, ?> params, final String... fuzzyNames) {
        return find(getEntityName(), params, fuzzyNames);
    }

    @Override
    public int countAll() {
        return countAll(getEntityName());
    }

    // 以下是对DependentDao的支持

    public QueryResult<T> find(final Class<?> dependedClass, final Serializable dependedKey,
            final QueryParameter parameter) {
        return find(getEntityName(), dependedClass, dependedKey, parameter);
    }

    public int delete(final Class<?> dependedClass, final Serializable dependedKey) {
        return delete(getEntityName(), dependedClass, dependedKey);
    }
}
