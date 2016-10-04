package org.truenewx.data.orm.dao.support.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.truenewx.core.tuple.Binate;
import org.truenewx.data.model.relation.SlicedRelation;
import org.truenewx.data.orm.dao.SlicedRelationDao;

/**
 * Hibernate切分关系DAO支持
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            关系类型
 * @param <L>
 *            左标识类型
 * @param <R>
 *            右标识类型
 * @param <S>
 *            切分者类型
 */
public abstract class HibernateSlicedRelationDaoSupport<T extends SlicedRelation<L, R, S>, L extends Serializable, R extends Serializable, S extends Serializable>
        extends HibernateSlicedDaoSupoort<T, S> implements SlicedRelationDao<T, L, R, S> {

    @Override
    @SuppressWarnings("unchecked")
    public T find(final S slicer, final L leftId, final R rightId) {
        if (leftId != null && rightId != null) {
            final Serializable id = buildId(leftId, rightId);
            final String entityName = getEntityName(slicer);
            if (id != null) {
                return (T) getDataAccessTemplate(entityName).getSession().get(entityName, id);
            }
            final Binate<String, String> binate = getIdProperty();
            if (binate != null) {
                final StringBuffer hql = new StringBuffer("from ").append(entityName)
                        .append(" r where r.").append(binate.getLeft()).append("=:leftId and r.")
                        .append(binate.getRight()).append("=:rightId");
                final Map<String, Object> params = new HashMap<>();
                params.put("leftId", leftId);
                params.put("rightId", rightId);
                return getDataAccessTemplate(entityName).first(hql, params);
            }
        }
        return null;
    }

    @Override
    public boolean exists(final S slicer, final L leftId, final R rightId) {
        if (leftId != null && rightId != null) {
            final Serializable id = buildId(leftId, rightId);
            final String entityName = getEntityName(slicer);
            if (id != null) {
                return getDataAccessTemplate(entityName).getSession().get(entityName, id) != null;
            }
            final Binate<String, String> binate = getIdProperty();
            if (binate != null) {
                final StringBuffer hql = new StringBuffer("select count(*) from ")
                        .append(entityName).append(" r where r.").append(binate.getLeft())
                        .append("=:leftId and r.").append(binate.getRight()).append("=:rightId");
                final Map<String, Object> params = new HashMap<>();
                params.put("leftId", leftId);
                params.put("rightId", rightId);
                return getDataAccessTemplate(entityName).count(hql, params) > 0;
            }
        }
        return false;
    }

    /**
     * 用左右标识构建单个标识对象
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
     * 获取标识属性对，left-左标识属性名，right-右表示属性名
     *
     * @return 标识属性对
     */
    protected Binate<String, String> getIdProperty() {
        return null;
    }

}
