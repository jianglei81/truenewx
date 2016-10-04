package org.truenewx.service.unity;

import java.io.Serializable;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.finder.UnityFinder;
import org.truenewx.data.model.unity.Unity;
import org.truenewx.service.Service;

/**
 * 基于单体的服务
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            单体标识类型
 */
public interface UnityService<T extends Unity<K>, K extends Serializable>
                extends Service, UnityFinder<T, K> {

    /**
     * 加载指定标识的单体，如果找不到则抛出异常
     *
     * @param id
     *            单体标识
     * @return 单体
     * @throws BusinessException
     *             如果找不到
     */
    T load(K id) throws BusinessException;

    /**
     * 删除单体
     *
     * @param id
     *            要删除的单体的标识
     * @throws HandleableException
     *             如果删除校验失败
     */
    void delete(K id) throws HandleableException;
}
