package org.truenewx.test.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.core.util.CollectionUtil;
import org.truenewx.data.orm.dao.EntityDao;
import org.truenewx.data.orm.dao.support.DaoFactory;
import org.truenewx.test.util.TestUtil;

/**
 * 数据初始化工厂
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class DataInitFactory {
    private Map<Class<?>, SortedSet<DataInitializer<?>>> initializers = new HashMap<>();
    @Autowired
    private DaoFactory daoFactory;

    /**
     * 注册数据初始化器
     *
     * @param initializer
     *
     * @author jianglei
     */
    void register(final DataInitializer<?> initializer) {
        final Class<?> modelClass = initializer.getModelClass();
        SortedSet<DataInitializer<?>> set;
        synchronized (this.initializers) {
            set = this.initializers.get(modelClass);
            if (set == null) {
                set = new TreeSet<>();
                this.initializers.put(modelClass, set);
            }
        }
        set.add(initializer);
    }

    /**
     * 初始化指定模型的数据
     *
     * @param modelClass
     *            数据模型类型
     *
     * @author jianglei
     */
    public void init(final Class<?> modelClass) {
        if (TestUtil.isTesting()) { // 只能在单元测试环境中运行
            final SortedSet<DataInitializer<?>> set = this.initializers.get(modelClass);
            if (set != null) {
                for (final DataInitializer<?> initializer : set) {
                    execute(initializer);
                }
            }
        }
    }

    /**
     * 执行指定的初始化器的初始化动作
     *
     * @param initializer
     *            初始化器
     */
    private void execute(final DataInitializer<?> initializer) {
        if (initializer != null) {
            final Class<?>[] depends = initializer.getDepends();
            if (depends != null) {
                for (final Class<?> depend : depends) {
                    init(depend);
                }
            }
            if (initializer.create()) {
                final Class<?>[] follows = initializer.getFollows();
                if (follows != null) {
                    for (final Class<?> follow : follows) {
                        init(follow);
                    }
                }
                initializer.update();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getDataList(final Class<T> modelClass) {
        final SortedSet<DataInitializer<?>> set = this.initializers.get(modelClass);
        if (set != null) {
            if (set.size() == 1) {
                return (List<T>) set.iterator().next().getDataList();
            } else {
                final List<T> list = new ArrayList<>();
                for (final DataInitializer<?> initializer : set) {
                    list.addAll((List<T>) initializer.getDataList());
                }
                return list;
            }
        } else {
            final EntityDao<T> dao = this.daoFactory.getDaoByEntityClass(modelClass);
            if (dao != null) {
                return dao.find((Map<String, ?>) null);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getDataList(final Class<T> modelClass, final int batch) {
        final SortedSet<DataInitializer<?>> set = this.initializers.get(modelClass);
        final DataInitializer<?> initializer = CollectionUtil.get(set, batch);
        if (initializer != null) {
            return (List<T>) initializer.getDataList();
        }
        return null;
    }

}
