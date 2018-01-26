package org.truenewx.service.unity;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.OwnedUnity;

/**
 * 从属基于传输模型的单体服务
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface OwnedModelUnityService<T extends OwnedUnity<K, O>, K extends Serializable, O extends Serializable>
                extends OwnedUnityService<T, K, O> {
    /**
     * 添加从属单体
     *
     * @param owner
     *            所属者
     * @param submitModel
     *            存放添加数据的提交模型对象
     * @return 添加的单体
     * @throws HandleableException
     *             如果添加校验失败
     */
    T add(O owner, SubmitModel<T> submitModel) throws HandleableException;

    /**
     * 修改从属单体<br/>
     * 注意：子类不应修改单体的所属者
     *
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
    T update(O owner, K id, SubmitModel<T> submitModel) throws HandleableException;
}
