package org.truenewx.service.dependent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.truenewx.core.annotation.Caption;
import org.truenewx.core.exception.BusinessException;
import org.truenewx.core.spring.beans.ContextInitializedBean;
import org.truenewx.data.orm.dao.DependentDao;
import org.truenewx.data.query.QueryParameter;
import org.truenewx.data.query.QueryResult;

/**
 * 依赖着服务实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
public class DependentServiceImpl implements DependentService, ContextInitializedBean {

    /**
     * key - 被依赖类型<br/>
     * value - 直接依赖的DAO集合
     */
    private Map<Class<?>, Set<DependentDao<?>>> dependentDaoMap = new HashMap<>();

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        @SuppressWarnings("rawtypes")
        final Map<String, DependentDao> daos = context.getBeansOfType(DependentDao.class);
        for (final DependentDao<?> dao : daos.values()) {
            final Class<?>[] dependedClasses = dao.getDependedClasses();
            if (dependedClasses != null) {
                for (final Class<?> dependedClass : dependedClasses) {
                    Set<DependentDao<?>> daoSet = this.dependentDaoMap.get(dependedClass);
                    if (daoSet == null) {
                        daoSet = new HashSet<>();
                        this.dependentDaoMap.put(dependedClass, daoSet);
                    }
                    daoSet.add(dao);
                }
            }
        }
    }

    @Override
    public Map<Class<?>, QueryResult<?>> find(final Class<?> dependedClass,
                    final Serializable dependedKey, final QueryParameter parameter,
                    final boolean recursive) {
        final Map<Class<?>, QueryResult<?>> result = new HashMap<>();
        find(dependedClass, dependedKey, parameter, recursive,
                        this.dependentDaoMap.get(dependedClass), result);
        return result;
    }

    private void find(final Class<?> entityClass, final Serializable key,
                    final QueryParameter parameter, final boolean recursive,
                    final Set<DependentDao<?>> dependentDaos,
                    final Map<Class<?>, QueryResult<?>> result) {
        if (dependentDaos != null) {
            for (final DependentDao<?> dependentDao : dependentDaos) {
                // 计算直接依赖实体的数量
                final QueryResult<?> qr = dependentDao.find(entityClass, key, parameter);
                if (!qr.isEmpty()) {
                    final Class<?> dependentClass = dependentDao.getEntityClass();
                    result.put(dependentClass, qr);

                    // 直接依赖实体数量为0时不再需要递归计算间接依赖实体的数量
                    if (recursive && qr.getPaging().getTotal() != 0) {
                        find(entityClass, key, parameter, recursive,
                                        this.dependentDaoMap.get(dependentClass), result);
                    }
                }
            }
        }
    }

    @Override
    public Map<Class<?>, Integer> delete(final Class<?> dependedClass,
                    final Serializable dependedKey) throws BusinessException {
        final Map<Class<?>, Integer> result = new HashMap<>();
        delete(dependedClass, dependedKey, this.dependentDaoMap.get(dependedClass), result);
        return result;
    }

    private void delete(final Class<?> dependedClass, final Serializable dependedKey,
                    final Set<DependentDao<?>> dependentDaos, final Map<Class<?>, Integer> result)
                    throws BusinessException {
        if (dependentDaos != null) {
            for (final DependentDao<?> dependentDao : dependentDaos) {
                try {
                    if (dependentDao.requiresPreDelete(dependedClass)) {
                        // 递归删除间接依赖实体
                        delete(dependedClass, dependedKey,
                                        this.dependentDaoMap.get(dependentDao.getEntityClass()),
                                        result);
                        // 删除直接依赖实体
                        final int count = dependentDao.delete(dependedClass, dependedKey);
                        if (count >= 0) { // 数量小于0视为无效
                            result.put(dependentDao.getEntityClass(), count);
                        }
                    }
                } catch (final UnsupportedOperationException e) {
                    final String entityCaption = getCaption(dependedClass);
                    final String dependentCaption = getCaption(dependentDao.getEntityClass());
                    throw new BusinessException(ERROR_UNDELETABLE, entityCaption, dependentCaption);
                }
            }
        }
    }

    private String getCaption(final Class<?> entityClass) {
        final Caption caption = entityClass.getAnnotation(Caption.class);
        if (caption != null) {
            return caption.value();
        }
        return "${constant.entity.class." + entityClass.getSimpleName() + "}";
    }

}
