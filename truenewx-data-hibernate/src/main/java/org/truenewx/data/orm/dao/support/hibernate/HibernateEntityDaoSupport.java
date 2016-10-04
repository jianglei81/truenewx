package org.truenewx.data.orm.dao.support.hibernate;

import org.hibernate.mapping.Column;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.util.BeanUtil;
import org.truenewx.data.orm.dao.support.EntityDaoSupport;
import org.truenewx.data.orm.hibernate.HibernateTemplate;
import org.truenewx.data.orm.hibernate.HibernateTemplateFactory;
import org.truenewx.data.orm.hibernate.LocalSessionFactoryRegistry;

/**
 * Hibernate通用DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            数据实体类型
 */
public abstract class HibernateEntityDaoSupport<T> extends EntityDaoSupport<T> {
    @Autowired
    private LocalSessionFactoryRegistry sessionFactoryRegistry;
    @Autowired
    private HibernateTemplateFactory hibernateTemplateFactory;

    @Override
    protected final HibernateTemplate getDataAccessTemplate(final String entityName) {
        return this.hibernateTemplateFactory.getHibernateTemplate(entityName);
    }

    protected final String getTableName(final String entityName) {
        return this.sessionFactoryRegistry.getTableName(entityName);
    }

    protected final Class<?> getPropertyClass(final String entityName, final String propertyName) {
        final Type type = this.sessionFactoryRegistry.getPropertyType(entityName, propertyName);
        return type == null ? null : type.getReturnedClass();
    }

    protected final Column getColumn(final String entityName, final String propertyName) {
        return this.sessionFactoryRegistry.getColumn(entityName, propertyName);
    }

    /**
     * 确保指定实体指定属性的最小数值
     *
     * @param entity
     *            实体
     * @param propertyName
     *            属性名
     * @param step
     *            属性数值增量
     *
     * @author jianglei
     */
    protected void ensurePropertyMinNumber(final T entity, final String propertyName,
                    final Number step) {
        final Number propertyValue = BeanUtil.getPropertyValue(entity, propertyName);
        // 此时的属性值必定为非空的数值
        if (step.doubleValue() > 0) { // 增量大于0，则属性值必须大于等于增量值
            if (propertyValue.doubleValue() < step.doubleValue()) {
                BeanUtil.setPropertyValue(entity, propertyName, step);
                save(entity);
            }
        } else { // 如果增量小于0，则属性值至少应等于0，如果允许属性值小于0，则不应调用本方法
            if (propertyValue.doubleValue() < 0) {
                BeanUtil.setPropertyValue(entity, propertyName, 0);
                save(entity);
            }
        }
    }
}
