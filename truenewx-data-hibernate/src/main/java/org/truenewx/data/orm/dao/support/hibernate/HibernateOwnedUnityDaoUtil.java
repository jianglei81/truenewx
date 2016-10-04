package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.data.model.unity.OwnedUnity;
import org.truenewx.data.orm.hibernate.HibernateTemplate;

/**
 * 具有所属者的单体DAO工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class HibernateOwnedUnityDaoUtil {

    private HibernateOwnedUnityDaoUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable> T find(
                    final HibernateTemplate hibernateTemplate, final String entityName,
                    final String ownerProperty, final O owner, final K id) {
        if (ownerProperty == null) {
            final T entity = (T) hibernateTemplate.getSession().get(entityName, id);
            if (entity != null && owner.equals(entity.getOwner())) {
                return entity;
            }
            return null;
        } else {
            final StringBuffer hql = new StringBuffer("from ").append(entityName)
                            .append(" e where e.").append(ownerProperty)
                            .append("=:owner and e.id=:id");
            final Map<String, Object> params = new HashMap<>();
            params.put("owner", owner);
            params.put("id", id);
            return hibernateTemplate.first(hql.toString(), params);
        }
    }

    public static int count(final HibernateTemplate hibernateTemplate, final String entityName,
                    final String ownerProperty, final Object owner) {
        if (ownerProperty == null) {
            throw new UnsupportedOperationException();
        }
        final StringBuffer hql = new StringBuffer("select count(*) from ").append(entityName)
                        .append(" e where e.").append(ownerProperty).append("=:owner");
        return hibernateTemplate.count(hql.toString(), "owner", owner);
    }

}
