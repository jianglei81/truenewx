package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;

import org.truenewx.data.model.unity.OwnedSlicedUnity;
import org.truenewx.data.orm.dao.OwnedSlicedUnityDao;
import org.truenewx.data.orm.hibernate.HibernateTemplate;

/**
 * Hibernate从属切分单体DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 * @param <S>
 *            切分者类型
 * @param <O>
 *            所属者类型
 */
public abstract class HibernateOwnedSlicedUnityDaoSupport<T extends OwnedSlicedUnity<K, S, O>, K extends Serializable, S extends Serializable, O extends Serializable>
                extends HibernateSlicedUnityDaoSupport<T, K, S>
                implements OwnedSlicedUnityDao<T, K, S, O> {

    @Override
    public T find(final S slicer, final O owner, final K id) {
        final String entityName = getEntityName(slicer);
        final HibernateTemplate hibernateTemplate = getDataAccessTemplate(entityName);
        final String ownerProperty = getOwnerProperty();
        return HibernateOwnedUnityDaoUtil.find(hibernateTemplate, entityName, ownerProperty, owner,
                        id);
    }

    @Override
    public int count(final S slicer, final O owner) {
        final String entityName = getEntityName(slicer);
        final HibernateTemplate hibernateTemplate = getDataAccessTemplate(entityName);
        final String ownerProperty = getOwnerProperty();
        return HibernateOwnedUnityDaoUtil.count(hibernateTemplate, entityName, ownerProperty,
                        owner);
    }

    /**
     * 获取所属者属性名<br/>
     * 默认返回null，此时通过标识获取单体后判断所属者是否匹配，可由子类覆写返回非null的值，从而通过所属字段限制单体查询<br/>
     * 建议，当所属者为引用对象下的属性，子类覆写提供非null的返回值，否则不覆写
     *
     * @return 所属者属性
     */
    protected String getOwnerProperty() {
        return null;
    }

}
