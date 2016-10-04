package org.truenewx.service.unity;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.unity.OwnedUnity;

/**
 * 具有所属者的简单的单体服务
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface OwnedSimpleUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
                extends OwnedUnityService<T, K, O> {
    /**
     * 添加具有所属者的单体
     *
     * @param owner
     *            所属者
     * @param unity
     *            存放添加数据的单体对象
     * @return 添加的单体
     * @throws HandleableException
     *             如果添加校验失败
     */
    T add(O owner, T unity) throws HandleableException;

    /**
     * 修改具有所属者的单体
     *
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
    T update(O owner, K id, T unity) throws HandleableException;
}
