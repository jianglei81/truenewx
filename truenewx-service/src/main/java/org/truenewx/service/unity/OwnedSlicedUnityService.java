package org.truenewx.service.unity;

import java.io.Serializable;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.finder.OwnedSlicedUnityFinder;
import org.truenewx.data.model.unity.OwnedSlicedUnity;
import org.truenewx.service.Service;

/**
 * 具有所属者的切分单体服务
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
public interface OwnedSlicedUnityService<T extends OwnedSlicedUnity<K, S, O>, K extends Serializable, S extends Serializable, O extends Serializable>
                extends Service, OwnedSlicedUnityFinder<T, K, S, O> {

    /**
     * 在指定切分者下，获取指定所属者和标识的单体，如果没找到则抛出异常
     *
     * @param slicer
     *            切分者
     * @param owner
     *            所属者
     * @param id
     *            标识
     * @return 单体
     * @throws BusinessException
     *             如果没找到
     */
    T load(S slicer, O owner, K id) throws BusinessException;

    /**
     * 在指定切分者下，删除具有所属者的单体
     *
     * @param slicer
     *            切分者
     * @param owner
     *            所属者
     * @param id
     *            要删除的单体的标识
     * @throws HandleableException
     *             如果删除校验失败
     */
    void delete(S slicer, O owner, K id) throws HandleableException;

}
