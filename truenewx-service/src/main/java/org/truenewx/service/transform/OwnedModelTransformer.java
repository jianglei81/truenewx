package org.truenewx.service.transform;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.OwnedUnity;

/**
 * 具有所属者的提交模型转换器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface OwnedModelTransformer<M extends SubmitModel<T>, T extends OwnedUnity<?, O>, O extends Serializable>
                extends ModelTransformer<M, T> {
    /**
     * 将指定提交模型中的数据转换到指定单体中
     *
     * @param owner
     *            所属者
     * @param submitModel
     *            提交模型
     * @param unity
     *            目标单体
     * @throws HandleableException
     *             如果转换过程中出现错误
     */
    void transform(O owner, M submitModel, T unity) throws HandleableException;
}
