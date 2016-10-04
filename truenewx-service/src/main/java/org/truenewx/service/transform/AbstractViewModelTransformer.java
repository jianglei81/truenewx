package org.truenewx.service.transform;

import java.util.ArrayList;
import java.util.Collection;

import org.truenewx.data.model.UnitaryEntity;
import org.truenewx.data.model.ViewModel;

/**
 * 抽象的视图模型转换器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractViewModelTransformer<T extends UnitaryEntity<?>, V extends ViewModel<T>>
                implements ViewModelTransformer<T, V> {

    @Override
    public Collection<V> transform(final Collection<T> entities) {
        if (entities != null) {
            final Collection<V> views = newViewCollection(entities);
            if (views != null) {
                for (final T entity : entities) {
                    final V view = transform(entity);
                    if (view != null) {
                        views.add(view);
                    }
                }
            }
            return views;
        }
        return null;
    }

    /**
     * 创建视图模型集合<br/>
     * 默认采用反射机制创建与实体集合类型相同的集合对象，子类可覆写，提供性能表现更好的实现
     *
     * @param entities
     *            实体集合
     * @return 视图模型集合
     */
    @SuppressWarnings("unchecked")
    protected Collection<V> newViewCollection(final Collection<T> entities) {
        try {
            // 默认采用与实体集合类型相同的集合
            return entities.getClass().newInstance();
        } catch (final ReflectiveOperationException e) {
            // 实体集合类型无法无参数创建对象，则简单的创建ArrayList
            return new ArrayList<>();
        }
    }
}
