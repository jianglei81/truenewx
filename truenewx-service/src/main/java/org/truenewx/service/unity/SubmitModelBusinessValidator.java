package org.truenewx.service.unity;

import java.io.Serializable;

import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.model.SubmitModel;
import org.truenewx.data.model.unity.Unity;

/**
 * 通过提交模型传递数据的业务逻辑校验器<br/>
 * 字段格式校验由格式校验框架完成，本接口的实现仅负责通过读取持久化数据验证字段数据的业务逻辑合法性
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SubmitModelBusinessValidator<M extends SubmitModel<T>, T extends Unity<K>, K extends Serializable> {

    /**
     * 验证指定id的指定提交模型数据的业务逻辑合法性
     *
     * @param id
     *            单体标识
     * @param model
     *            提交模型数据
     * @throws HandleableException
     *             如果验证失败
     */
    // 方法名中含有Business字样，是为了凸显验证业务逻辑而不是格式，同时也减少对外暴露RPC接口时与其它方法重名的可能性
    void validateBusiness(K id, M model) throws HandleableException;

}
