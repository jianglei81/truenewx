package org.truenewx.service;

import org.truenewx.core.util.ClassUtil;

/**
 * 抽象服务
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            关联类型
 */
public abstract class AbstractService<T> extends ServiceSupport implements Service {

    /**
     * 确保指定模型非null
     *
     * @param model
     *            模型
     *
     * @return 非null的模型
     */
    protected T ensureNonnull(T model) {
        if (model == null) { // 如果此时实体为null，则使用实体类的构造函数创建一个新的实体
            final Class<T> modelClass = ClassUtil.getActualGenericType(getClass(), 0);
            try {
                model = modelClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return model;
    }

    /**
     * 获取实体不存在的错误码，在load()方法中无法取得实体时抛出异常用，默认抛出空指针异常
     *
     * @return 实体不存在的错误码
     */
    protected String getNonexistentErorrCode() {
        throw new NullPointerException();
    }

}
