package org.truenewx.service.transform;

import org.truenewx.data.model.Model;

/**
 * 模型转换器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <S>
 *            源模型类型
 * @param <T>
 *            目标模型类型
 */
public interface ModelTransformer<S extends Model, T extends Model> {

}
