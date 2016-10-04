package org.truenewx.service.unity;

import java.io.Serializable;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.finder.SlicedUnityFinder;
import org.truenewx.data.model.unity.SlicedUnity;
import org.truenewx.service.Service;

/**
 * 切分单体服务
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
public interface SlicedUnityService<T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable>
                extends Service, SlicedUnityFinder<T, K, S> {

    /**
     * 在指定切分者下根据标识获取单体，如果找不到则抛出异常
     *
     * @param slicer
     *            切分者
     * @param id
     *            标识
     * @return 单体
     * @throws BusinessException
     *             如果找不到
     */
    T load(S slicer, K id) throws BusinessException;

    /**
     * 在指定切分者下删除指定单体
     *
     * @param slicer
     *            切分者
     * @param id
     *            要删除的单体的标识
     * @throws HandleableException
     *             如果删除校验失败
     */
    void delete(S slicer, K id) throws HandleableException;

}
