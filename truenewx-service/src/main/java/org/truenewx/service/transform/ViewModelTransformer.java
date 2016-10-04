package org.truenewx.service.transform;

import java.util.Collection;

import org.truenewx.data.model.Entity;
import org.truenewx.data.model.ViewModel;

/**
 * 视图模型转换器。将实体模型转换为视图模型
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface ViewModelTransformer<T extends Entity, V extends ViewModel<T>>
                extends ModelTransformer<T, V> {
    /**
     * 将指定实体转换为视图模型
     *
     * @param entity
     *            实体
     * @return 视图模型
     */
    V transform(T entity);

    /**
     * 将指定实体集合转换为视图模型集合
     *
     * @param entities
     *            实体集合
     * @return 视图模型集合
     */
    Collection<V> transform(Collection<T> entities);
}
