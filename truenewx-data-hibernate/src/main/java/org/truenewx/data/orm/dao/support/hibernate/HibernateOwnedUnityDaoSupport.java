package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.core.Strings;
import org.truenewx.data.model.unity.OwnedUnity;
import org.truenewx.data.orm.dao.OwnedUnityDao;
import org.truenewx.data.orm.hibernate.HibernateTemplate;

/**
 * Hibernate从属单体的DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T> 单体类型
 * @param <K> 标识类型
 * @param <O> 所属者类型
 */
public abstract class HibernateOwnedUnityDaoSupport<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends HibernateUnityDaoSupport<T, K> implements OwnedUnityDao<T, K, O> {
    @Override
    public T find(O owner, K id) {
        if (id == null) {
            return null;
        }
        String entityName = getEntityName();
        HibernateTemplate hibernateTemplate = getHibernateTemplate();
        String ownerProperty = getOwnerProperty();
        return HibernateOwnedUnityDaoUtil.find(hibernateTemplate, entityName, ownerProperty, owner,
                id);
    }

    @Override
    public int count(O owner) {
        HibernateTemplate hibernateTemplate = getHibernateTemplate();
        String entityName = getEntityName();
        String ownerProperty = getOwnerProperty();
        return HibernateOwnedUnityDaoUtil.count(hibernateTemplate, entityName, ownerProperty,
                owner);
    }

    @Override
    public T increaseNumber(O owner, K id, String propertyName, Number step) {
        double stepValue = step.doubleValue();
        if (stepValue != 0) { // 增量不为0时才处理
            String entityName = getEntityName();
            StringBuffer hql = new StringBuffer("update ").append(entityName).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append("+:step where id=:id");
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("step", step);

            String ownerProperty = getOwnerProperty();
            if (owner != null && ownerProperty != null) {
                hql.append(" and ").append(ownerProperty).append("=:owner");
                params.put("owner", owner);
            }

            if (doIncreaseNumber(entityName, hql, params, propertyName, stepValue)) {
                // 更新字段后需刷新实体
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
