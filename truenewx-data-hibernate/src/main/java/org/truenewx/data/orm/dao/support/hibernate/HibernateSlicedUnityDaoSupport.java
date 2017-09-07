package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.core.Strings;
import org.truenewx.data.model.unity.SlicedUnity;
import org.truenewx.data.orm.dao.SlicedUnityDao;

/**
 * Hibernate分区单体DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 * @param <S>
 *            切分者类型
 */
public abstract class HibernateSlicedUnityDaoSupport<T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable>
        extends HibernateSlicedDaoSupoort<T, S> implements SlicedUnityDao<T, K, S> {

    @Override
    @SuppressWarnings("unchecked")
    public final T find(final S slicer, final K id) {
        if (id != null) {
            final String entityName = getEntityName(slicer);
            return (T) getDataAccessTemplate(entityName).getSession().get(entityName, id);
        }
        return null;
    }

    @Override
    public T increaseNumber(final S slicer, final K id, final String propertyName,
            final Number step) {
        if (step.doubleValue() != 0) { // 增量为0时不处理
            final String entityName = getEntityName(slicer);
            final StringBuffer hql = new StringBuffer("update ").append(entityName).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append(Strings.PLUS).append(":step where id=:id");
            final Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("step", step);
            if (getDataAccessTemplate(entityName).update(hql, params) > 0) {
                // 直接更新字段后需刷新实体
                final T unity = find(slicer, id);
                try {
                    refresh(unity);
                } catch (final Exception e) { // 忽略刷新失败
                    this.logger.error(e.getMessage(), e);
                }
                ensurePropertyMinNumber(unity, propertyName, step);
                return unity;
            }
        }
        return null;
    }

}
