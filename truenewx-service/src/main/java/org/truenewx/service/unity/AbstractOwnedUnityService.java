package org.truenewx.service.unity;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.springframework.util.Assert;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.OwnedUnity;
import org.truenewx.data.orm.dao.OwnedUnityDao;

/**
 * 抽象的从属单体的服务
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 * @param <O>
 *            所属者类型
 */
public abstract class AbstractOwnedUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends AbstractUnityService<T, K>
        implements OwnedSimpleUnityService<T, K, O>, OwnedModelUnityService<T, K, O> {

    @Override
    public T find(final O owner, final K id) {
        return getDao().find(owner, id);
    }

    @Override
    public T load(final O owner, final K id) throws BusinessException {
        final T unity = find(owner, id);
        assertNotNull(unity);
        return unity;
    }

    @Override
    public T add(final O owner, T unity) throws HandleableException {
        if (owner == null) {
            return null;
        }
        unity = beforeSave(owner, null, unity);
        if (unity != null) {
            Assert.isTrue(owner.equals(unity.getOwner()), "owner must equal unity's owner");
            getDao().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 注意：子类不应修改单体的所属者
     */
    @Override
    public T update(final O owner, final K id, T unity) throws HandleableException {
        if (owner == null || id == null) {
            return null;
        }
        unity = beforeSave(owner, id, unity);
        if (unity != null) {
            Assert.isTrue(owner.equals(unity.getOwner()) && id.equals(unity.getId()),
                    "owner must equal unity's owner, and id must equal unity's id");
            getDao().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 在保存添加/修改有所属者的单体前调用，负责验证改动的单体数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数
     *
     * @param owner
     *            所属者
     * @param id
     *            要修改的单体标识，为null时表示是添加动作
     * @param unity
     *            存放添加/修改数据的单体对象
     * @return 已写入数据，即将保存的单体
     * @throws HandleableException
     *             如果数据验证失败
     */
    protected T beforeSave(final O owner, final K id, final T unity) throws HandleableException {
        if (id != null) { // 如果子类不覆写update()方法，则不支持修改
            throw new UnsupportedOperationException();
        }
        beforeAdd(owner, unity);
        return unity;
    }

    /**
     * 在添加从属单体前调用，由子类覆写
     *
     * @param owner
     *            所属者
     * @param unity
     *            存放添加数据的单体对象
     * @throws HandleableException
     *             如果校验不通过
     */
    protected void beforeAdd(final O owner, final T unity) throws HandleableException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T add(final O owner, final SubmitModel<T> submitModel) throws HandleableException {
        if (owner == null) {
            return null;
        }
        final T unity = beforeSave(owner, null, submitModel);
        if (unity != null) {
            Assert.isTrue(owner.equals(unity.getOwner()), "owner must equal unity's owner");
            getDao().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    @Override
    public T update(final O owner, final K id, final SubmitModel<T> submitModel)
            throws HandleableException {
        if (owner == null || id == null) {
            return null;
        }
        final T unity = beforeSave(owner, id, submitModel);
        if (unity != null) {
            Assert.isTrue(owner.equals(unity.getOwner()) && id.equals(unity.getId()),
                    "owner must equal unity's owner, and id must equal unity's id");
            getDao().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 在保存添加/修改有所属者的单体前调用，负责验证改动的模型数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数
     *
     * @param owner
     *            所属者
     * @param id
     *            要修改的单体标识，为null时表示是添加动作
     * @param submitModel
     *            存放添加/修改数据的单体对象
     * @return 已写入数据，即将保存的单体
     * @throws HandleableException
     *             如果数据验证失败
     */
    protected T beforeSave(final O owner, final K id, final SubmitModel<T> submitModel)
            throws HandleableException {
        T unity;
        if (id == null) {
            unity = beforeAdd(owner, submitModel);
            unity = ensureNotNull(unity);
        } else {
            unity = beforeUpdate(owner, id, submitModel);
            if (unity == null) {
                unity = find(owner, id);
            }
        }
        return unity;
    }

    /**
     * 在添加从属单体前调用，由子类覆写<br/>
     * {@link #transform(Serializable, SubmitModel, OwnedUnity)}
     * 方法会被上层调用，子类覆写本方法时可不调用该方法
     *
     * @param owner
     *            所属者
     * @param submitModel
     *            存放添加数据的传输对象
     * @return 要添加的单体，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    @Nullable
    protected T beforeAdd(final O owner, final SubmitModel<T> submitModel)
            throws HandleableException {
        throw new UnsupportedOperationException();
    }

    /**
     * 在修改从属单体前调用，由子类覆写<br/>
     * {@link #transform(Serializable, SubmitModel, OwnedUnity)}
     * 方法会被上层调用，子类覆写本方法时可不调用该方法<br/>
     * 注意：子类不应在此修改单体的所属者
     *
     * @param owner
     *            所属者
     * @param id
     *            要修改单体的标识
     * @param submitModel
     *            存放修改数据的传输对象
     * @return 要修改的单体，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    @Nullable
    protected T beforeUpdate(final O owner, final K id, final SubmitModel<T> submitModel)
            throws HandleableException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final O owner, final K id) throws HandleableException {
        if (owner != null && id != null) {
            T unity = beforeDelete(owner, id);
            if (unity == null) {
                unity = find(owner, id);
            }
            getDao().delete(unity);
        }
    }

    /**
     * 根据标识删除从属单体前调用，由子类覆写<br/>
     * 子类不覆写或调用父类的本方法，将无法删除单体
     *
     * @param owner
     *            所属者
     * @param id
     *            要删除的单体的标识
     * @return 要删除的单体，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    protected T beforeDelete(final O owner, final K id) throws HandleableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected abstract OwnedUnityDao<T, K, O> getDao();

}
