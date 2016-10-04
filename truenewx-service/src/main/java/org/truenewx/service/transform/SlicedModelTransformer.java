package org.truenewx.service.transform;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.SlicedUnity;

/**
 * 切分提交模型转换器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SlicedModelTransformer<M extends SubmitModel<T>, T extends SlicedUnity<?, S>, S extends Serializable>
                extends ModelTransformer<M, T> {
    /**
     * 将指定提交模型中的数据转换到指定单体中
     *
     * @param slicer
     *            切分者
     * @param model
     *            提交模型
     * @param unity
     *            目标单体
     * @throws HandleableException
     *             如果转换过程中出现错误
     */
    void transform(S slicer, M model, T unity) throws HandleableException;
}
