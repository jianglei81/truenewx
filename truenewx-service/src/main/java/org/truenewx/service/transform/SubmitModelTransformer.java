package org.truenewx.service.transform;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.Entity;
import org.truenewx.data.model.SubmitModel;

/**
 * 提交模型转换器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SubmitModelTransformer<M extends SubmitModel<T>, T extends Entity>
        extends ModelTransformer<M, T> {
    /**
     * 将指定提交模型中的数据转换到指定实体中
     *
     * @param submitModel
     *            提交模型
     * @param entity
     *            目标实体
     * @throws HandleableException
     *             如果转换过程中出现错误
     */
    void transform(M submitModel, T entity) throws HandleableException;
}
