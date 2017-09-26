package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.truenewx.data.model.SlicedEntity;
import org.truenewx.data.orm.dao.SlicedDao;
import org.truenewx.data.orm.hibernate.HibernateTemplate;
import org.truenewx.data.query.QueryParameter;
import org.truenewx.data.query.QueryResult;

/**
 * Hibernate切分DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            实体类型
 * @param <S>
 *            切分者类型
 */
public abstract class HibernateSlicedDaoSupoort<T extends SlicedEntity<S>, S extends Serializable>
        extends HibernateEntityDaoSupport<T> implements SlicedDao<T, S> {

    protected final HibernateTemplate getHibernateTemplate(final S slicer) {
        final String entityName = getEntityName(slicer);
        return getDataAccessTemplate(entityName);
    }

    @Override
    public final void delete(final T entity) {
        if (entity != null) {
            final S slicer = entity.getSlicer();
            if (slicer != null) {
                final String entityName = getEntityName(slicer);
                getDataAccessTemplate(entityName).getSession().delete(entityName, entity);
            }
        }
    }

    @Override
    public final void save(final T entity) {
        if (entity != null) {
            final S slicer = entity.getSlicer();
            final String entityName = getEntityName(slicer);
            getDataAccessTemplate(entityName).getSession().saveOrUpdate(entityName, entity);
        }
    }

    @Override
    public final void refresh(final T entity) {
        if (entity != null) {
            final S slicer = entity.getSlicer();
            if (slicer != null) {
                final String entityName = getEntityName(slicer);
                getDataAccessTemplate(entityName).getSession().refresh(entityName, entity);
            }
        }
    }

    @Override
    public final void flush(final S slicer) {
        if (slicer != null) {
            getHibernateTemplate(slicer).getSession().flush();
        }
    }

    @Override
    public final List<T> find(final Map<String, ?> params, final String... fuzzyNames) {
        final List<T> list = new ArrayList<>();
        for (final S slice : getSlices()) {
            list.addAll(find(getEntityName(slice), params, fuzzyNames));
        }
        return list;
    }

    @Override
    public int countAll() {
        int count = 0;
        for (final S slice : getSlices()) {
            count += countAll(getEntityName(slice));
        }
        return count;
    }

    /**
     * 根据切分者获取实体名称
     *
     * @param slicer
     *            切分者
     * @return 实体名称
     */
    protected abstract String getEntityName(final S slicer);

    /**
     * 获取全部切片清单，通过以该清单中的切片作为切分者可以遍历所有切分表
     *
     * @return 全部切片清单
     */
    protected abstract S[] getSlices();

    // 以下是对DependentDao的支持

    /**
     * 根据被依赖实体获取切分者标识<br/>
     * 一般情况下，切分实体所依赖的实体即为切分者，如有特殊，子类可覆写
     *
     * @param dependedClass
     *            被依赖实体的类型
     * @param dependedKey
     *            被依赖实体的标识
     * @return 切分者标识
     */
    @SuppressWarnings("unchecked")
    protected S getSlicer(final Class<?> dependedClass, final Serializable dependedKey) {
        return (S) dependedKey;
    }

    @Override
    public T first(final S slicer) {
        return first(getEntityName(slicer));
    }

    public QueryResult<T> find(final Class<?> dependedClass, final Serializable dependedKey,
            final QueryParameter parameter) {
        final String entityName = getEntityName(getSlicer(dependedClass, dependedKey));
        return find(entityName, dependedClass, dependedKey, parameter);
    }

    public int delete(final Class<?> dependedClass, final Serializable dependedKey) {
        final String entityName = getEntityName(getSlicer(dependedClass, dependedKey));
        return delete(entityName, dependedClass, dependedKey);
    }
}
