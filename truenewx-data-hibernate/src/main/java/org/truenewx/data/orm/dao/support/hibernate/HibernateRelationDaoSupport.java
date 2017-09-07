package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.LockOptions;
import org.truenewx.core.Strings;
import org.truenewx.core.tuple.Binate;
import org.truenewx.data.model.relation.Relation;
import org.truenewx.data.orm.dao.RelationDao;

/**
 * HibernateDAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            类型
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

    @Override
    public T increaseNumber(final L leftId, final R rightId, final String propertyName,
            final Number step) {
        final Number maxValue = getNumberPropertyMaxValue(propertyName);
        if (maxValue != null && step.doubleValue() != 0) { // 属性为数值类型且增量不为0时才处理
            final Binate<String, String> idProperty = getIdProperty();
            final String leftIdProperty = idProperty.getLeft();
            final String rightIdProperty = idProperty.getRight();
            StringBuffer hql = new StringBuffer("update ").append(getEntityName()).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append(Strings.PLUS).append(":step where ").append(leftIdProperty)
                    .append("=:leftId and ").append(rightIdProperty).append("=:rightId and ")
                    .append(propertyName + "+:step<=:maxValue");
            final Map<String, Object> params = new HashMap<>();
            params.put("leftId", leftId);
            params.put("rightId", rightId);
            params.put("step", step);
            params.put("maxValue", maxValue);
            if (getHibernateTemplate().update(hql, params) == 0) { // 如果没有更新到记录，有可能是修改数值超出字段允许的最大值
                // 此时，需要将数值字段修改为允许的最大值
                hql = new StringBuffer("update ").append(getEntityName()).append(" set ")
                        .append(propertyName).append(Strings.EQUAL).append(":maxValue where ")
                        .append(leftIdProperty).append("=:leftId and ").append(rightIdProperty)
                        .append("=:rightId and ").append(propertyName + "+:step>:maxValue");
                if (getHibernateTemplate().update(hql, params) == 0) {
                    // 如果还是没有更新到记录，说明根据id无法找到单体，直接返回null
                    return null;
                }
            }
            // 更新字段后需刷新实体
            final T realtion = find(leftId, rightId);
            try {
                refresh(realtion);
            } catch (final Exception e) { // 忽略刷新失败
                this.logger.error(e.getMessage(), e);
            }
            ensurePropertyMinNumber(realtion, propertyName, step);
            return realtion;
        }
        return null;
    }
}
