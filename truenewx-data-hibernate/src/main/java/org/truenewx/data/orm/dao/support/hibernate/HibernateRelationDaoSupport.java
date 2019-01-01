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
    public T find(L leftId, R rightId) {
        if (leftId != null && rightId != null) {
            Serializable id = buildId(leftId, rightId);
            String entityName = getEntityName();
            if (id != null) {
                return (T) getHibernateTemplate().getSession().get(entityName, id);
            }
            Binate<String, String> binate = getIdProperty();
            if (binate != null) {
                StringBuffer hql = new StringBuffer("from ").append(entityName)
                        .append(" r where r.").append(binate.getLeft()).append("=:leftId and r.")
                        .append(binate.getRight()).append("=:rightId");
                Map<String, Object> params = new HashMap<>();
                params.put("leftId", leftId);
                params.put("rightId", rightId);
                return getHibernateTemplate().first(hql, params);
            }
        }
        return null;
    }

    @Override
    public boolean exists(L leftId, R rightId) {
        if (leftId != null && rightId != null) {
            Serializable id = buildId(leftId, rightId);
            String entityName = getEntityName();
            if (id != null) {
                return getHibernateTemplate().getSession().get(entityName, id) != null;
            }
            Binate<String, String> binate = getIdProperty();
            if (binate != null) {
                StringBuffer hql = new StringBuffer("select count(*) from ").append(entityName)
                        .append(" r where r.").append(binate.getLeft()).append("=:leftId and r.")
                        .append(binate.getRight()).append("=:rightId");
                Map<String, Object> params = new HashMap<>();
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
    protected Serializable buildId(L leftId, R rightId) {
        return null;
    }

    /**
     * 获取标识属性对，left-左标识属性名，right-右标识属性名<br/>
     * 当左右标识中有一个为引用实体的属性字段时，子类应该覆写该方法
     *
     * @return 标识属性对
     */
    protected Binate<String, String> getIdProperty() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    protected T get(L leftId, R rightId, LockOptions lockOption) {
        Serializable id = buildId(leftId, rightId);
        if (id != null) {
            return (T) getHibernateTemplate().getSession().get(getEntityName(), id, lockOption);
        }
        return null;
    }

    @Override
    public T increaseNumber(L leftId, R rightId, String propertyName, Number step) {
        Number maxValue = getNumberPropertyMaxValue(propertyName);
        if (maxValue != null && step.doubleValue() != 0) { // 属性为数值类型且增量不为0时才处理
            Binate<String, String> idProperty = getIdProperty();
            String leftIdProperty = idProperty.getLeft();
            String rightIdProperty = idProperty.getRight();
            StringBuffer hql = new StringBuffer("update ").append(getEntityName()).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append(Strings.PLUS).append(":step where ").append(leftIdProperty)
                    .append("=:leftId and ").append(rightIdProperty).append("=:rightId and ")
                    .append(propertyName + "+:step<=:maxValue");
            Map<String, Object> params = new HashMap<>();
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
            T realtion = find(leftId, rightId);
            try {
                refresh(realtion);
            } catch (Exception e) { // 忽略刷新失败
                this.logger.error(e.getMessage(), e);
            }
            ensurePropertyMinNumber(realtion, propertyName, step);
            return realtion;
        }
        return null;
    }
}
