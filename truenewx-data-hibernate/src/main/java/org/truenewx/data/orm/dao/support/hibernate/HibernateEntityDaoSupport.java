package org.truenewx.data.orm.dao.support.hibernate;

import java.util.Map;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.mapping.Column;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.MathUtil;
import org.truenewx.data.model.support.ModelPropertyLimitValueManager;
import org.truenewx.data.orm.dao.support.EntityDaoSupport;
import org.truenewx.data.orm.hibernate.HibernateTemplate;
import org.truenewx.data.orm.hibernate.HibernateTemplateFactory;
import org.truenewx.data.orm.hibernate.LocalSessionFactoryRegistry;

/**
 * Hibernate通用DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T> 数据实体类型
 */
public abstract class HibernateEntityDaoSupport<T> extends EntityDaoSupport<T> {

    @Autowired
    private LocalSessionFactoryRegistry sessionFactoryRegistry;
    @Autowired
    private HibernateTemplateFactory hibernateTemplateFactory;
    @Autowired
    private ModelPropertyLimitValueManager propertyLimitValueManager;

    @Override
    public final HibernateTemplate getDataAccessTemplate(String entityName) {
        return this.hibernateTemplateFactory.getHibernateTemplate(entityName);
    }

    protected final String getTableName(String entityName) {
        return this.sessionFactoryRegistry.getTableName(entityName);
    }

    protected final Column getColumn(String entityName, String propertyName) {
        return this.sessionFactoryRegistry.getColumn(entityName, propertyName);
    }

    private Number getNumberPropertyMinValue(String entityName, String propertyName) {
        Class<T> entityClass = getEntityClass();
        if (this.propertyLimitValueManager.isNonNumber(entityClass, propertyName)) {
            // 已明确知晓不是数字类型的属性，直接返回null
            return null;
        }
        Number minValue = this.propertyLimitValueManager.getMinValue(entityClass, propertyName);
        if (minValue == null) {
            Class<?> propertyClass = getPropertyClass(propertyName);
            minValue = MathUtil.minValue(propertyClass);
            if (minValue != null) { // 可从类型取得最小值，说明是数值类型
                Min min = ClassUtil.findAnnotation(getEntityClass(), propertyName, Min.class);
                if (min != null) {
                    minValue = min.value();
                } else {
                    DecimalMin decimalMin = ClassUtil.findAnnotation(getEntityClass(), propertyName,
                            DecimalMin.class);
                    if (decimalMin != null) {
                        minValue = MathUtil.parseDecimal(decimalMin.value(), null);
                    } else {
                        Range range = ClassUtil.findAnnotation(getEntityClass(), propertyName,
                                Range.class);
                        if (range != null) {
                            minValue = range.min();
                        }
                    }
                }

                @SuppressWarnings("unchecked")
                Class<? extends Number> type = (Class<? extends Number>) propertyClass;
                Column column = getColumn(entityName, propertyName);
                int precision = column.getPrecision();
                int scale = column.getScale();
                Number minValue2 = MathUtil.minValue(type, precision, scale);
                // 两个最小值中的较大者，才是实际允许的最小值
                if (minValue2.doubleValue() > minValue.doubleValue()) {
                    minValue = minValue2;
                }
            }
            this.propertyLimitValueManager.putMinValue(entityClass, propertyName, minValue);
        }
        return minValue;
    }

    private Number getNumberPropertyMaxValue(String entityName, String propertyName) {
        Class<T> entityClass = getEntityClass();
        if (this.propertyLimitValueManager.isNonNumber(entityClass, propertyName)) {
            // 已明确知晓不是数字类型的属性，直接返回null
            return null;
        }
        Number maxValue = this.propertyLimitValueManager.getMaxValue(entityClass, propertyName);
        if (maxValue == null) {
            Class<?> propertyClass = getPropertyClass(propertyName);
            maxValue = MathUtil.maxValue(propertyClass);
            if (maxValue != null) { // 可从类型取得最大值，说明是数值类型
                Max max = ClassUtil.findAnnotation(getEntityClass(), propertyName, Max.class);
                if (max != null) {
                    maxValue = max.value();
                } else {
                    DecimalMax decimalMax = ClassUtil.findAnnotation(getEntityClass(), propertyName,
                            DecimalMax.class);
                    if (decimalMax != null) {
                        maxValue = MathUtil.parseDecimal(decimalMax.value(), null);
                    } else {
                        Range range = ClassUtil.findAnnotation(getEntityClass(), propertyName,
                                Range.class);
                        if (range != null) {
                            maxValue = range.max();
                        }
                    }
                }

                @SuppressWarnings("unchecked")
                Class<? extends Number> type = (Class<? extends Number>) propertyClass;
                Column column = getColumn(entityName, propertyName);
                int precision = column.getPrecision();
                int scale = column.getScale();
                Number maxValue2 = MathUtil.maxValue(type, precision, scale);
                // 两个最大值中的较小者，才是实际允许的最大值
                if (maxValue2.doubleValue() < maxValue.doubleValue()) {
                    maxValue = maxValue2;
                }
            }
            this.propertyLimitValueManager.putMaxValue(entityClass, propertyName, maxValue);
        }
        return maxValue;
    }

    protected final boolean doIncreaseNumber(String entityName, StringBuffer hql,
            Map<String, Object> params, String propertyName, double stepValue) {
        if (stepValue < 0) { // 增量为负时需限定最小值
            Number minValue = getNumberPropertyMinValue(entityName, propertyName);
            if (minValue == null) { // 无法取得属性类型最小值，说明属性不是数值类型
                return false;
            }
            hql.append(" and ").append(propertyName).append("+:step>=:minValue");
            params.put("minValue", minValue);
        } else { // 增量为正时需限定最大值
            Number maxValue = getNumberPropertyMaxValue(entityName, propertyName);
            if (maxValue == null) { // 无法取得属性类型最大值，说明属性不是数值类型
                return false;
            }
            hql.append(" and ").append(propertyName).append("+:step<=:maxValue");
            params.put("maxValue", maxValue);
        }
        return getDataAccessTemplate(entityName).update(hql, params) > 0;
    }

}
