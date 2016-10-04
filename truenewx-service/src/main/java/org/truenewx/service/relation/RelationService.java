package org.truenewx.service.relation;

import java.io.Serializable;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.data.finder.RelationFinder;
import org.truenewx.data.model.relation.Relation;
import org.truenewx.service.Service;

/**
 * 关系服务
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            关系类型
 * @param <L>
 *            左标识类型
 * @param <R>
 *            右标识类型
 */
public interface RelationService<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
                extends Service, RelationFinder<T, L, R> {
    /**
     * 根据标识获取关系，如果找不到则抛出异常
     *
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @return 关系
     * @throws BusinessException
     *             如果找不到
     */
    T load(L leftId, R rightId) throws BusinessException;

    /**
     * 删除关系
     *
     * @param leftId
     *            左标识
     * @param rightId
     *            右标识
     * @throws HandleableException
     *             如果删除校验失败
     */
    void delete(L leftId, R rightId) throws HandleableException;
}
