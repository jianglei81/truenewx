package org.truenewx.service.unity;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.SlicedUnity;

/**
 * 基于传输模型的切分单体服务
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
public interface ModelSlicedUnityService<T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable>
                extends SlicedUnityService<T, K, S> {

    /**
     * 在指定切分者下添加单体
     *
     * @param submitModel
     *            存放添加数据的提交模型对象
     * @return 添加的单体
     * @throws HandleableException
     *             如果添加校验失败
     */
    T add(S slicer, SubmitModel<T> submitModel) throws HandleableException;

    /**
     * 在指定切分者下修改单体
     *
     * @param slicer
     *            切分者
     * @param id
     *            要修改单体的标识
     * @param submitModel
     *            存放修改数据的提交模型对象
     * @return 修改后的单体
     *
     * @throws HandleableException
     *             如果修改校验失败
     */
    T update(S slicer, K id, SubmitModel<T> submitModel) throws HandleableException;

}
