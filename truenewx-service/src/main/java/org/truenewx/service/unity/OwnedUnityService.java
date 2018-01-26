package org.truenewx.service.unity;

import java.io.Serializable;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.finder.OwnedUnityFinder;
import org.truenewx.data.model.unity.OwnedUnity;

/**
 * 从属单体服务
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
public interface OwnedUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
        extends UnityService<T, K>, OwnedUnityFinder<T, K, O> {
    /**
     * 加载指定所属者和标识的单体，如果没找到则抛出异常
     *
     * @param owner
     *            所属者
     * @param id
     *            标识
     * @return 单体
     * @throws BusinessException
     *             如果没找到
     */
    T load(O owner, K id) throws BusinessException;

    /**
     * 删除从属单体
     *
     * @param owner
     *            所属者
     * @param id
     *            要删除的单体的标识
     * @throws HandleableException
     *             如果删除校验失败
     */
    void delete(O owner, K id) throws HandleableException;
}
