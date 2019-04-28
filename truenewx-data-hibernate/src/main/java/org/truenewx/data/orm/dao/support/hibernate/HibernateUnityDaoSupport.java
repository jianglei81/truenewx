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
 * @param <T> 单体类型
 * @param <K> 标识类型
 */
public abstract class HibernateUnityDaoSupport<T extends Unity<K>, K extends Serializable>
        extends HibernateDaoSupport<T> implements UnityDao<T, K> {

    @Override
    @SuppressWarnings("unchecked")
    public final T find(K id) {
        if (id != null) {
            return (T) getHibernateTemplate().getSession().get(getEntityName(), id);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected final T find(K id, LockOptions lockOption) {
        return (T) getHibernateTemplate().getSession().get(getEntityName(), id, lockOption);
    }

    @Override
    public T increaseNumber(K id, String propertyName, Number step) {
        double stepValue = step.doubleValue();
        if (stepValue != 0) { // 增量不为0时才处理
            String entityName = getEntityName();
            StringBuffer hql = new StringBuffer("update ").append(entityName).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append("+:step where id=:id");
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("step", step);

            if (doIncreaseNumber(entityName, hql, params, propertyName, stepValue)) {
                // 正确更新字段后需刷新实体
                T unity = find(id);
                try {
                    refresh(unity);
                } catch (Exception e) { // 忽略刷新失败
                    this.logger.error(e.getMessage(), e);
                }
                return unity;
            }
        }
        return null;
    }

}
