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
 * 依赖者服务实现
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
    public void afterInitialized(ApplicationContext context) throws Exception {
        @SuppressWarnings("rawtypes")
        Map<String, DependentDao> daos = context.getBeansOfType(DependentDao.class);
        for (DependentDao<?> dao : daos.values()) {
            Class<?>[] dependedClasses = dao.getDependedClasses();
            if (dependedClasses != null) {
                for (Class<?> dependedClass : dependedClasses) {
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
    public Map<Class<?>, QueryResult<?>> find(Class<?> dependedClass, Serializable dependedKey,
            QueryParameter parameter, boolean recursive) {
        Map<Class<?>, QueryResult<?>> result = new HashMap<>();
        find(dependedClass, dependedKey, parameter, recursive,
                this.dependentDaoMap.get(dependedClass), result);
        return result;
    }

    private void find(Class<?> entityClass, Serializable key, QueryParameter parameter,
            boolean recursive, Set<DependentDao<?>> dependentDaos,
            Map<Class<?>, QueryResult<?>> result) {
        if (dependentDaos != null) {
            for (DependentDao<?> dependentDao : dependentDaos) {
                // 计算直接依赖实体的数量
                QueryResult<?> qr = dependentDao.find(entityClass, key, parameter);
                if (qr.getRecords().size() > 0) {
                    Class<?> dependentClass = dependentDao.getEntityClass();
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
    public Map<Class<?>, Integer> delete(Class<?> dependedClass, Serializable dependedKey)
            throws BusinessException {
        Map<Class<?>, Integer> result = new HashMap<>();
        delete(dependedClass, dependedKey, this.dependentDaoMap.get(dependedClass), result);
        return result;
    }

    private void delete(Class<?> dependedClass, Serializable dependedKey,
            Set<DependentDao<?>> dependentDaos, Map<Class<?>, Integer> result)
            throws BusinessException {
        if (dependentDaos != null) {
            for (DependentDao<?> dependentDao : dependentDaos) {
                try {
                    if (dependentDao.requiresPreDelete(dependedClass)) {
                        // 递归删除间接依赖实体
                        delete(dependedClass, dependedKey,
                                this.dependentDaoMap.get(dependentDao.getEntityClass()), result);
                        // 删除直接依赖实体
                        int count = dependentDao.delete(dependedClass, dependedKey);
                        if (count >= 0) { // 数量小于0视为无效
                            result.put(dependentDao.getEntityClass(), count);
                        }
                    }
                } catch (UnsupportedOperationException e) {
                    String entityCaption = getCaption(dependedClass);
                    String dependentCaption = getCaption(dependentDao.getEntityClass());
                    throw new BusinessException(ERROR_UNDELETABLE, entityCaption, dependentCaption);
                }
            }
        }
    }

    private String getCaption(Class<?> entityClass) {
        Caption caption = entityClass.getAnnotation(Caption.class);
        if (caption != null) {
            return caption.value();
        }
        return "${constant.entity.class." + entityClass.getSimpleName() + "}";
    }

}
