package org.truenewx.service.unity;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.OwnedSlicedUnity;

/**
 * 具有所属者的基于传输模型的切分单体服务
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
public interface OwnedModelSlicedUnityService<T extends OwnedSlicedUnity<K, S, O>, K extends Serializable, S extends Serializable, O extends Serializable>
                extends OwnedSlicedUnityService<T, K, S, O> {

    /**
     * 在指定切分者下添加具有所属者的单体
     *
     * @param slicer
     *            切分者
     * @param owner
     *            所属者
     * @param submitModel
     *            存放添加数据的提交模型对象
     * @return 添加的单体
     * @throws HandleableException
     *             如果添加校验失败
     */
    T add(S slicer, O owner, SubmitModel<T> submitModel) throws HandleableException;

    /**
     * 在指定切分者下修改具有所属者的单体<br/>
     * 注意：子类不应修改单体的所属者
     *
     * @param slicer
     *            切分者
     * @param owner
     *            所属者
     * @param id
     *            要修改单体的标识
     * @param submitModel
     *            存放修改数据的提交模型对象
     * @return 修改后的单体
     *
     * @throws HandleableException
     *             如果修改校验失败
     */
    T update(S slicer, O owner, K id, SubmitModel<T> submitModel) throws HandleableException;

}
