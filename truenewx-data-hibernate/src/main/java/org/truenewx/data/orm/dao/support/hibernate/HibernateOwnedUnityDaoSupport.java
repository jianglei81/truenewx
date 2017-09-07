package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.core.Strings;
import org.truenewx.data.model.unity.OwnedUnity;
import org.truenewx.data.orm.dao.OwnedUnityDao;
import org.truenewx.data.orm.hibernate.HibernateTemplate;

/**
 * Hibernate具有所属者的单体的DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 * @param <O>
 *            所属者类型
 */
public abstract class HibernateOwnedUnityDaoSupport<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends HibernateUnityDaoSupport<T, K> implements OwnedUnityDao<T, K, O> {
    @Override
    public T find(final O owner, final K id) {
        final String entityName = getEntityName();
        final HibernateTemplate hibernateTemplate = getHibernateTemplate();
        final String ownerProperty = getOwnerProperty();
        return HibernateOwnedUnityDaoUtil.find(hibernateTemplate, entityName, ownerProperty, owner,
                id);
    }

    @Override
    public int count(final O owner) {
        final HibernateTemplate hibernateTemplate = getHibernateTemplate();
        final String entityName = getEntityName();
        final String ownerProperty = getOwnerProperty();
        return HibernateOwnedUnityDaoUtil.count(hibernateTemplate, entityName, ownerProperty,
                owner);
    }

    @Override
    public T increaseNumber(final O owner, final K id, final String propertyName,
            final Number step) {
        if (step.doubleValue() != 0) { // 增量为0时不处理
            final StringBuffer hql = new StringBuffer("update ").append(getEntityName())
                    .append(" set ").append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append(Strings.PLUS).append(":step where id=:id");
            final Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("step", step);
            final String ownerProperty = getOwnerProperty();
            if (ownerProperty != null && owner != null) {
                hql.append(" and ").append(ownerProperty).append("=:owner");
                params.put("owner", owner);
            }
            if (getHibernateTemplate().update(hql, params) > 0) {
                // 直接更新字段后需刷新实体
                final T unity = find(owner, id);
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

    /**
     * 获取所属者属性名<br/>
     * 默认返回null，此时通过标识获取单体后判断所属者是否匹配，可由子类覆写返回非null的值，从而通过所属字段限制单体查询<br/>
     * 建议：当所属者为引用对象下的属性时 ，子类覆写提供非null的返回值，否则不覆写
     *
     * @return 所属者属性
     */
    protected String getOwnerProperty() {
        return null;
    }

}
