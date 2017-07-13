package org.truenewx.service.relation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.springframework.util.Assert;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.relation.Relation;
import org.truenewx.data.model.unity.Unity;
import org.truenewx.data.orm.dao.RelationDao;
import org.truenewx.service.AbstractService;
import org.truenewx.service.transform.SubmitModelTransformer;

/**
 * 抽象关系服务
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
public abstract class AbstractRelationService<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends AbstractService<T> implements SimpleRelationService<T, L, R>,
        ModelRelationService<T, L, R>, SubmitModelTransformer<SubmitModel<T>, T> {

    @Override
    public T find(final L leftId, final R rightId) {
        return getDao().find(leftId, rightId);
    }

    @Override
    public boolean exists(final L leftId, final R rightId) {
        return getDao().exists(leftId, rightId);
    }

    @Override
    public List<T> find(final Map<String, ?> params, final String... fuzzyNames) {
        return getDao().find(params, fuzzyNames);
    }

    @Override
    public T load(final L leftId, final R rightId) throws BusinessException {
        final T relation = find(leftId, rightId);
        assertNotNull(relation);
        return relation;
    }

    @Override
    public T add(final L leftId, final R rightId, T relation) throws HandleableException {
        relation = beforeAdd(leftId, rightId, relation);
        if (relation != null) {
            getDao().save(relation);
            afterSave(relation);
        }
        return relation;
    }

    /**
     * 在添加关系前调用，由子类覆写
     *
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @param relation
     *            存放添加数据的关系对象
     *
     * @return 要添加的关系，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    protected T beforeAdd(final L leftId, final R rightId, final T relation)
            throws HandleableException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T update(final L leftId, final R rightId, final T relation) throws HandleableException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T add(final L leftId, final R rightId, final SubmitModel<T> model)
            throws HandleableException {
        T relation = beforeAdd(leftId, rightId, model);
        relation = ensureNotNull(relation);
        if (relation != null) {
            transform(model, relation);

            getDao().save(relation);
            afterSave(relation);
        }
        return relation;
    }

    /**
     * 在添加关系前调用，由子类覆写<br/>
     * {@link #transform(SubmitModel, Unity)}方法会被上层调用，子类覆写本方法时可不调用该方法
     *
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @param model
     *            存放添加数据的提交模型对象
     *
     *
     * @return 要添加的关系，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    @Nullable
    protected T beforeAdd(final L leftId, final R rightId, final SubmitModel<T> model)
            throws HandleableException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T update(final L leftId, final R rightId, final SubmitModel<T> model)
            throws HandleableException {
        if (leftId == null || rightId == null) {
            return null;
        }
        T relation = beforeUpdate(leftId, rightId, model);
        if (relation == null) {
            relation = find(leftId, rightId);
        }
        if (relation != null) {
            transform(model, relation);

            Assert.isTrue(
                    relation.getLeftId().equals(leftId) && relation.getRightId().equals(rightId));
            getDao().save(relation);
        }
        return relation;
    }

    /**
     * 在修改关系前调用，由子类覆写<br/>
     * {@link #transform(SubmitModel, Unity)}方法会被上层调用，子类覆写本方法时可不调用该方法
     *
     * @param id
     *            要修改关系的标识
     * @param model
     *            存放修改数据的提交模型对象
     * @return 要修改的关系，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    @Nullable
    protected T beforeUpdate(final L leftId, final R rightId, final SubmitModel<T> model)
            throws HandleableException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final L leftId, final R rightId) throws HandleableException {
        T relation = beforeDelete(leftId, rightId);
        if (relation == null) {
            relation = find(leftId, rightId);
        }
        getDao().delete(relation);
    }

    /**
     * 根据标识删除关系前调用，由子类覆写<br/>
     * 不覆写或子类调用父类的本方法，将无法删除关系
     *
     * @param id
     *            要删除的关系的标识
     * @return 要删除的关系，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    @Nullable
    protected T beforeDelete(final L leftId, final R rightId) throws HandleableException {
        throw new UnsupportedOperationException();
    }

    /**
     * 关系保存之后调用，负责后续处理
     *
     * @param relation
     *            被保存的关系
     * @throws HandleableException
     *             如果处理过程中出现错误
     */
    protected void afterSave(final T relation) throws HandleableException {
    }

    @Override
    public void transform(final SubmitModel<T> submitModel, final T relation)
            throws HandleableException {
    }

    protected abstract RelationDao<T, L, R> getDao();

}
