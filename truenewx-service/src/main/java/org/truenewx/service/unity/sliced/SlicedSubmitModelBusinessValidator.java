package org.truenewx.service.unity.sliced;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.SlicedUnity;

/**
 * 通过切分提交模型传递数据的业务逻辑校验器<br/>
 * 字段格式校验由格式校验框架完成，本接口的实现仅负责通过读取持久化数据验证字段数据的业务逻辑合法性
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SlicedSubmitModelBusinessValidator<M extends SubmitModel<T>, T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable> {

    /**
     * 验证指定id的指定提交模型数据的业务逻辑合法性
     *
     * @param slicer
     *            切分者
     * @param id
     *            单体标识
     * @param model
     *            提交模型数据
     * @throws HandleableException
     *             如果验证失败
     */
    void validateBusiness(S slicer, K id, M model) throws HandleableException;

}
