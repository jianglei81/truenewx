package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.LockOptions;
import org.truenewx.core.Strings;
import org.truenewx.data.model.unity.Unity;
import org.truenewx.data.orm.dao.UnityDao;

/**
 * Hibernate单体DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 */
public abstract class HibernateUnityDaoSupport<T extends Unity<K>, K extends Serializable>
                extends HibernateDaoSupport<T> implements UnityDao<T, K> {

    @Override
    @SuppressWarnings("unchecked")
    public final T find(final K id) {
        if (id != null) {
            return (T) getHibernateTemplate().getSession().get(getEntityName(), id);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected final T find(final K id, final LockOptions lockOption) {
        return (T) getHibernateTemplate().getSession().get(getEntityName(), id, lockOption);
    }

    @Override
    public T increaseNumber(final K id, final String propertyName, final Number step) {
        final Number maxValue = getNumberPropertyMaxValue(propertyName);
        if (maxValue != null && step.doubleValue() != 0) { // 属性为数值类型且增量不为0时才处理
            StringBuffer hql = new StringBuffer("update ").append(getEntityName()).append(" set ")
                            .append(propertyName).append(Strings.EQUAL).append(propertyName)
                            .append(Strings.PLUS).append(":step where id=:id and ")
                            .append(propertyName + "+:step<=:maxValue");
            final Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("step", step);
            params.put("maxValue", maxValue);
            if (getHibernateTemplate().update(hql, params) == 0) { // 如果没有更新到记录，有可能是修改数值超出字段允许的最大值
                // 此时，需要将数值字段修改为允许的最大值
                hql = new StringBuffer("update ").append(getEntityName()).append(" set ")
                                .append(propertyName).append(Strings.EQUAL)
                                .append(":maxValue where id=:id and ")
                                .append(propertyName + "+:step>:maxValue");
                if (getHibernateTemplate().update(hql, params) == 0) {
                    // 如果还是没有更新到记录，说明根据id无法找到单体，直接返回null
                    return null;
                }
            }
            // 更新字段后需刷新实体
            final T unity = find(id);
            try {
                refresh(unity);
            } catch (final Exception e) { // 忽略刷新失败
                e.printStackTrace();
            }
            ensurePropertyMinNumber(unity, propertyName, step);
            return unity;
        }
        return null;
    }

}
