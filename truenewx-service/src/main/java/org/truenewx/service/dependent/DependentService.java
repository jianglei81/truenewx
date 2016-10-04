package org.truenewx.service.dependent;

import java.io.Serializable;
import java.util.Map;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.data.query.QueryParameter;
import org.truenewx.data.query.QueryResult;
import org.truenewx.service.Service;

/**
 * 依赖者服务
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface DependentService extends Service {

    /**
     * 错误码：不可删除
     */
    String ERROR_UNDELETABLE = "error.dependent.undeletable";

    /**
     * 计算依赖指定实体的各类型实体的数量
     *
     * @param dependedClass
     *            被依赖实体的类型
     * @param dependedKey
     *            被依赖实体的标识
     * @param parameter
     *            TODO
     * @param recursive
     *            是否递归计算，false - 仅计算直接依赖的实体数量，true - 计算全部直接和间接依赖的实体数量
     * @return 各类型实体的数量，key - 实体类型，value - 实体数量
     */
    Map<Class<?>, QueryResult<?>> find(Class<?> dependedClass, Serializable dependedKey,
                    QueryParameter parameter, boolean recursive);

    /**
     * 删除所有直接和间接依赖指定实体的实体数据，不包括指定实体本身
     *
     * @param dependedClass
     *            被依赖实体的类型
     * @param dependedKey
     *            被依赖实体的标识
     * @return 被删除的各类型实体的数量，key - 实体类型，value - 实体数量
     * @throws BusinessException
     *             如果有数据不能删除
     */
    Map<Class<?>, Integer> delete(Class<?> dependedClass, Serializable dependedKey)
                    throws BusinessException;
}
