package org.truenewx.data.orm.dao.support.hibernate;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.mapping.Column;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.MathUtil;
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

    @Override
    protected final HibernateTemplate getDataAccessTemplate(String entityName) {
        return this.hibernateTemplateFactory.getHibernateTemplate(entityName);
    }

    protected final String getTableName(String entityName) {
        return this.sessionFactoryRegistry.getTableName(entityName);
    }

    protected final Column getColumn(String entityName, String propertyName) {
        return this.sessionFactoryRegistry.getColumn(entityName, propertyName);
    }

    protected Number getNumberPropertyMinValue(String propertyName) {
        Class<?> propertyClass = getPropertyClass(propertyName);
        if (Number.class.isAssignableFrom(propertyClass)) {
            Number minValue = null;

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
            return minValue;
        }
        return null;
    }

    protected Number getNumberPropertyMaxValue(String propertyName) {
        Class<?> propertyClass = getPropertyClass(propertyName);
        if (Number.class.isAssignableFrom(propertyClass)) {
            Number maxValue = null;

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
            return maxValue;
        }
        return null;
    }

}
