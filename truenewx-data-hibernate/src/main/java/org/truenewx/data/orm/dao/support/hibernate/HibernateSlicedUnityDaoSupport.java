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
 * @param <T> 单体类型
 * @param <K> 标识类型
 * @param <S> 切分者类型
 */
public abstract class HibernateSlicedUnityDaoSupport<T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable>
        extends HibernateSlicedDaoSupoort<T, S> implements SlicedUnityDao<T, K, S> {

    @Override
    @SuppressWarnings("unchecked")
    public final T find(S slicer, K id) {
        if (id != null) {
            String entityName = getEntityName(slicer);
            return (T) getDataAccessTemplate(entityName).getSession().get(entityName, id);
        }
        return null;
    }

    @Override
    public T increaseNumber(S slicer, K id, String propertyName, Number step) {
        double stepValue = step.doubleValue();
        if (stepValue != 0) { // 增量不为0时才处理
            String entityName = getEntityName(slicer);
            StringBuffer hql = new StringBuffer("update ").append(entityName).append(" set ")
                    .append(propertyName).append(Strings.EQUAL).append(propertyName)
                    .append("+:step where id=:id and ");
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("step", step);

            if (stepValue < 0) { // 增量为负时需限定最小值
                Number minValue = getNumberPropertyMinValue(entityName, propertyName);
                hql.append(propertyName).append("+:step>=:minValue");
                params.put("minValue", minValue);
            } else { // 增量为正时需限定最大值
                Number maxValue = getNumberPropertyMaxValue(entityName, propertyName);
                hql.append(propertyName).append("+:step<=:maxValue");
                params.put("maxValue", maxValue);
            }
            if (getHibernateTemplate(entityName).update(hql, params) == 0) {
                return null;
            }
            // 更新字段后需刷新实体
            T unity = find(slicer, id);
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
