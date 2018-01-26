package org.truenewx.service.unity.sliced;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.unity.OwnedSlicedUnity;

/**
 * 从属简单的切分单体服务
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
public interface OwnedSimpleSlicedUnityService<T extends OwnedSlicedUnity<K, S, O>, K extends Serializable, S extends Serializable, O extends Serializable>
                extends OwnedSlicedUnityService<T, K, S, O> {

    /**
     * 在指定切分者下添加从属单体
     *
     * @param slicer
     *            切分者
     * @param owner
     *            所属者
     * @param unity
     *            存放添加数据的单体对象
     * @return 添加的单体
     * @throws HandleableException
     *             如果添加校验失败
     */
    T add(S slicer, O owner, T unity) throws HandleableException;

    /**
     * 在指定切分者下修改从属单体
     *
     * @param slicer
     *            切分者
     * @param owner
     *            所属者
     * @param id
     *            要修改单体的标识
     * @param unity
     *            存放修改数据的单体对象
     * @return 修改后的单体
     * @throws HandleableException
     *             如果修改校验失败
     */
    T update(S slicer, O owner, K id, T unity) throws HandleableException;

}
