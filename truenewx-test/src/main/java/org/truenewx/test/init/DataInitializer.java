package org.truenewx.test.init;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.data.model.Entity;
import org.truenewx.data.orm.dao.EntityDao;

/**
 * 数据初始化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class DataInitializer<T extends Entity> implements Comparable<DataInitializer<T>> {
    /**
     * 数据初始化工厂
     */
    protected DataInitFactory factory;
    /**
     * 初始化数据集合
     */
    private List<T> dataList;

    @Autowired
    public final void setDataInitFactory(final DataInitFactory factory) {
        factory.register(this);
        this.factory = factory;
    }

    protected Class<T> getModelClass() {
        return ClassUtil.getActualGenericType(getClass(), 0);
    }

    protected Class<?>[] getDepends() {
        return new Class<?>[0];
    }

    /**
     * 创建初始化数据
     *
     * @return 是否创建，当此前已经初始化时返回false
     */
    protected abstract boolean create();

    protected Class<?>[] getFollows() {
        return new Class<?>[0];
    }

    protected void update() {
    }

    protected void setDataList(final List<T> dataList) {
        this.dataList = dataList;
    }

    List<T> getDataList() {
        return this.dataList;
    }

    @SuppressWarnings("unchecked")
    protected final <M, C extends M> C getData(final Class<M> modelClass, final int index) {
        final List<M> list = this.factory.getDataList(modelClass);
        return list == null ? null : (C) list.get(index);
    }

    @SuppressWarnings("unchecked")
    protected final <M, C extends M> C getData(final Class<M> modelClass, final int batch,
            final int index) {
        final List<M> list = this.factory.getDataList(modelClass, batch);
        return list == null ? null : (C) list.get(index);
    }

    protected int getOrdinal() {
        return 1;
    }

    @Override
    public final int compareTo(final DataInitializer<T> other) {
        return Integer.valueOf(getOrdinal()).compareTo(other.getOrdinal());
    }

    /**
     *
     * @return 数据模型DAO
     *
     * @author jianglei
     */
    protected abstract EntityDao<T> getDao();

    /**
     *
     * @return 是否已经初始化
     *
     * @author jianglei
     */
    protected boolean isInitialized() {
        return getDao().countAll() > 0;
    }

}
