package org.truenewx.service.transform;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.OwnedSlicedUnity;

/**
 * 具有所属者的切分提交模型转换器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface OwnedSlicedModelTransformer<M extends SubmitModel<T>, T extends OwnedSlicedUnity<?, S, O>, S extends Serializable, O extends Serializable>
                extends ModelTransformer<M, T> {
    /**
     * 将指定提交模型中的数据转换到指定实体中
     *
     * @param slicer
     *            切分者
     * @param owner
     *            所属者
     * @param model
     *            提交模型
     * @param unity
     *            目标实体
     * @throws HandleableException
     *             如果转换过程中出现错误
     */
    void transform(S slicer, O owner, M model, T unity) throws HandleableException;
}
