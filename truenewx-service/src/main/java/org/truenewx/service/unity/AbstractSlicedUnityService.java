package org.truenewx.service.unity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.springframework.util.Assert;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.SlicedUnity;
import org.truenewx.data.orm.dao.SlicedUnityDao;
import org.truenewx.service.AbstractService;
import org.truenewx.service.transform.SlicedModelTransformer;

/**
 * 抽象的切分单体服务
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 * @param <S>
 *            切分者类型
 */
public abstract class AbstractSlicedUnityService<T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable>
        extends AbstractService<T> implements SimpleSlicedUnityService<T, K, S>,
        ModelSlicedUnityService<T, K, S>, SlicedModelTransformer<SubmitModel<T>, T, S> {

    @Override
    public T find(final S slicer, final K id) {
        return getDao().find(slicer, id);
    }

    @Override
    public List<T> find(final Map<String, ?> params, final String... fuzzyNames) {
        return getDao().find(params, fuzzyNames);
    }

    @Override
    public T load(final S slicer, final K id) throws BusinessException {
        final T unity = find(slicer, id);
        assertNotNull(unity);
        return unity;
    }

    @Override
    public T add(final S slicer, T unity) throws HandleableException {
        if (slicer == null) {
            return null;
        }
        unity = beforeSave(slicer, null, unity);
        if (unity != null) {
            Assert.isTrue(slicer.equals(unity.getSlicer()), "slicer must equal unity's slicer");
            getDao().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    @Override
    public T update(final S slicer, final K id, T unity) throws HandleableException {
        if (slicer == null || id == null) {
            return null;
        }
        unity = beforeSave(slicer, id, unity);
        if (unity != null) {
            Assert.isTrue(slicer.equals(unity.getSlicer()) && id.equals(unity.getId()),
                    "slicer must equal unity's slicer, and id must equal unity's id");
            getDao().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 在保存添加/修改单体前调用，负责验证改动的单体数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数
     *
     * @param slicer
     *            切分者
     * @param id
     *            要修改的单体标识，为null时表示是添加动作
     * @param unity
     *            存放添加/修改数据的单体对象
     * @return 已写入数据，即将保存的单体
     * @throws HandleableException
     *             如果数据验证失败
     */
    protected T beforeSave(final S slicer, final K id, final T unity) throws HandleableException {
        if (id != null) { // 如果子类不覆写update()方法，则不支持修改
            throw new UnsupportedOperationException();
        }
        beforeAdd(slicer, unity);
        return unity;
    }

    /**
     * 在添加单体前调用，由子类覆写
     *
     * @param slicer
     *            切分者
     * @param unity
     *            存放添加数据的单体对象
     * @throws HandleableException
     *             如果校验不通过
     */
    protected void beforeAdd(final S slicer, final T unity) throws HandleableException {
        throw new UnsupportedOperationException();
    }

    /**
     * 单体保存之后调用，负责后续处理
     *
     * @param unity
     *            被保存的单体
     * @throws HandleableException
     *             如果处理过程中出现错误
     */
    protected void afterSave(final T unity) throws HandleableException {
    }

    @Override
    public T add(final S slicer, final SubmitModel<T> submitModel) throws HandleableException {
        final T unity = beforeSave(slicer, null, submitModel);
        if (unity != null) {
            Assert.isTrue(slicer.equals(unity.getSlicer()), "slicer must equal unity's slicer");
            getDao().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    @Override
    public T update(final S slicer, final K id, final SubmitModel<T> submitModel)
            throws HandleableException {
        if (id == null) {
            return null;
        }
        final T unity = beforeSave(slicer, id, submitModel);
        if (unity != null) {
            Assert.isTrue(slicer.equals(unity.getSlicer()) && id.equals(unity.getId()),
                    "slicer must equal unity's slicer, and id must equal unity's id");
            getDao().save(unity);
            afterSave(unity);
        }
        return unity;
    }

    /**
     * 在保存添加/修改单体前调用，负责验证改动的模型数据，并写入返回的结果单体中<br/>
     * <strong>注意：</strong>子类覆写时应确保结果不为null（否则将不保存），且结果单体的标识等于传入的指定标识参数
     *
     * @param slicer
     *            切分者
     * @param id
     *            要修改的单体标识，为null时表示是添加动作
     * @param submitModel
     *            存放添加/修改数据的单体对象
     * @return 已写入数据，即将保存的单体
     * @throws HandleableException
     *             如果数据验证失败
     */
    protected T beforeSave(final S slicer, final K id, final SubmitModel<T> submitModel)
            throws HandleableException {
        T unity;
        if (id == null) {
            unity = beforeAdd(slicer, submitModel);
            unity = ensureNotNull(unity);
        } else {
            unity = beforeUpdate(slicer, id, submitModel);
            if (unity == null) {
                unity = find(slicer, id);
            }
        }
        if (unity != null) {
            transform(slicer, submitModel, unity);
        }
        return unity;
    }

    /**
     * 在添加单体前调用，由子类覆写<br/>
     * {@link #transform(Serializable, SubmitModel, SlicedUnity)}
     * 方法会被上层调用，子类覆写本方法时可不调用该方法
     *
     * @param slicer
     *            切分者
     * @param model
     *            存放添加数据的提交模型对象
     * @return 要添加的单体，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    @Nullable
    protected T beforeAdd(final S slicer, final SubmitModel<T> model) throws HandleableException {
        throw new UnsupportedOperationException();
    }

    /**
     * 在修改单体前调用，由子类覆写<br/>
     * {@link #transform(Serializable, SubmitModel, SlicedUnity)}
     * 方法会被上层调用，子类覆写本方法时可不调用该方法
     *
     * @param slicer
     *            切分者
     * @param id
     *            要修改单体的标识
     * @param model
     *            存放修改数据的提交模型对象
     * @return 要修改的单体，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    @Nullable
    protected T beforeUpdate(final S slicer, final K id, final SubmitModel<T> model)
            throws HandleableException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transform(final S slicer, final SubmitModel<T> model, final T unity)
            throws HandleableException {
    }

    @Override
    public void delete(final S slicer, final K id) throws HandleableException {
        T unity = beforeDelete(slicer, id);
        if (unity == null) {
            unity = find(slicer, id);
        }
        getDao().delete(unity);
    }

    /**
     * 在指定切分者下根据标识删除单体前调用，由子类覆写<br/>
     * 不覆写或子类调用父类的本方法，将无法删除单体
     *
     * @param slicer
     *            切分者
     * @param id
     *            要删除的单体的标识
     * @return 要删除的单体，可返回null，返回非null值有助于提高性能
     * @throws HandleableException
     *             如果校验不通过
     */
    @Nullable
    protected T beforeDelete(final S slicer, final K id) throws HandleableException {
        throw new UnsupportedOperationException();
    }

    protected abstract SlicedUnityDao<T, K, S> getDao();

}
