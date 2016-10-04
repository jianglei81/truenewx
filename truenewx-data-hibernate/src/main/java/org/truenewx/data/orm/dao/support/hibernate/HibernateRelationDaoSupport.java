package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.LockOptions;
import org.truenewx.core.tuple.Binate;
import org.truenewx.data.model.relation.Relation;
import org.truenewx.data.orm.dao.RelationDao;

/**
 * Hibernate关系DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            关系类型
 * @param <L>
 *            左标识类型
 * @param <R>
 *            右标识类型
 */
public abstract class HibernateRelationDaoSupport<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends HibernateDaoSupport<T> implements RelationDao<T, L, R> {

    @Override
    @SuppressWarnings("unchecked")
    public T find(final L leftId, final R rightId) {
        if (leftId != null && rightId != null) {
            final Serializable id = buildId(leftId, rightId);
            final String entityName = getEntityName();
            if (id != null) {
                return (T) getHibernateTemplate().getSession().get(entityName, id);
            }
            final Binate<String, String> binate = getIdProperty();
            if (binate != null) {
                final StringBuffer hql = new StringBuffer("from ").append(entityName)
                        .append(" r where r.").append(binate.getLeft()).append("=:leftId and r.")
                        .append(binate.getRight()).append("=:rightId");
                final Map<String, Object> params = new HashMap<>();
                params.put("leftId", leftId);
                params.put("rightId", rightId);
                return getHibernateTemplate().first(hql, params);
            }
        }
        return null;
    }

    @Override
    public boolean exists(final L leftId, final R rightId) {
        if (leftId != null && rightId != null) {
            final Serializable id = buildId(leftId, rightId);
            final String entityName = getEntityName();
            if (id != null) {
                return getHibernateTemplate().getSession().get(entityName, id) != null;
            }
            final Binate<String, String> binate = getIdProperty();
            if (binate != null) {
                final StringBuffer hql = new StringBuffer("select count(*) from ")
                        .append(entityName).append(" r where r.").append(binate.getLeft())
                        .append("=:leftId and r.").append(binate.getRight()).append("=:rightId");
                final Map<String, Object> params = new HashMap<>();
                params.put("leftId", leftId);
                params.put("rightId", rightId);
                return getHibernateTemplate().count(hql, params) > 0;
            }
        }
        return false;
    }

    /**
     * 用左右标识构建单个标识对象<br/>
     * 当左右标识均为当前实体下的直接属性字段时，子类应该覆写该方法
     *
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @return 单个标识对象
     */
    protected Serializable buildId(final L leftId, final R rightId) {
        return null;
    }

    /**
     * 获取标识属性对，left-左标识属性名，right-右标识属性名<br/>
     * 当左右标识中有一个为引用实体的属性字段时，子类应该覆写该方法
     *
     * @return 标识属性对
     */
    protected Binate<String, String> getIdProperty() {
        return null;
    }

    @SuppressWarnings("unchecked")
    protected final T get(final L leftId, final R rightId, final LockOptions lockOption) {
        final Serializable id = buildId(leftId, rightId);
        if (id != null) {
            return (T) getHibernateTemplate().getSession().get(getEntityName(), id, lockOption);
        }
        return null;
    }
}
