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
 * @param <T> 类型
 * @param <L> 左标识类型
 * @param <R> 右标识类型
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
     * @param leftId  左标识
     * @param rightId 右标识
     * @return 单个标识对象
     */
    @Deprecated
    private Serializable buildId(L leftId, R rightId) {
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
        double stepValue = step.doubleValue();
        if (stepValue != 0) { // 增量不为0时才处理
            String entityName = getEntityName();
            Binate<String, String> idProperty = getIdProperty();
            String leftIdProperty = idProperty.getLeft();
            String rightIdProperty = idProperty.getRight();
            StringBuffer hql = new StringBuffer("update ").append(entityName).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append("+:step where ").append(leftIdProperty).append(Strings.EQUAL)
                    .append("=:leftId and ").append(rightIdProperty).append("=:rightId and ");
            Map<String, Object> params = new HashMap<>();
            params.put("leftId", leftId);
            params.put("rightId", rightId);
            params.put("step", step);

            if (stepValue < 0) { // 增量为负时需限定最小值
                Number minValue = getNumberPropertyMinValue(propertyName);
                hql.append(propertyName).append("+:step>=:minValue");
                params.put("minValue", minValue);
            } else { // 增量为正时需限定最大值
                Number maxValue = getNumberPropertyMaxValue(propertyName);
                hql.append(propertyName).append("+:step<=:maxValue");
                params.put("maxValue", maxValue);
            }
            if (getHibernateTemplate().update(hql, params) == 0) {
                return null;
            }
            // 更新字段后需刷新实体
            T unity = find(leftId, rightId);
            try {
                refresh(unity);
            } catch (Exception e) { // 忽略刷新失败
                this.logger.error(e.getMessage(), e);
            }
            return unity;
        }
        return null;
    }
}
