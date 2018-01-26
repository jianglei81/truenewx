package org.truenewx.service.unity.sliced;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.unity.SlicedUnity;

/**
 * 简单的切分单体服务
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
public interface SimpleSlicedUnityService<T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable>
                extends SlicedUnityService<T, K, S> {

    /**
     * 在指定切分者下添加单体
     *
     * @param slicer
     *            切分者
     * @param unity
     *            存放添加数据的单体对象
     * @return 添加成功的单体
     * @throws HandleableException
     *             如果添加校验失败
     */
    T add(S slicer, T unity) throws HandleableException;

    /**
     * 在指定切分者下修改单体
     *
     * @param slicer
     *            切分者
     * @param id
     *            要修改单体的标识
     * @param unity
     *            存放修改数据的单体对象
     * @return 修改后的单体
     * @throws HandleableException
     *             如果修改校验失败
     */
    T update(S slicer, K id, T unity) throws HandleableException;

}
