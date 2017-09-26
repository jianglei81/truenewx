package org.truenewx.data.orm.dao;

import java.io.Serializable;

import org.truenewx.core.model.Sliced;

/**
 * 切分DAO
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SlicedDao<T extends Sliced<S>, S extends Serializable> extends EntityDao<T> {

    /**
     * 获取已有数据中指定切分者下的第一条，常用于单元测试，谨慎用于它处
     *
     * @return 指定切分者下的第一条数据记录
     */
    T first(S slicer);

    /**
     * 强制将缓存中的指定切分者下的数据同步至数据库
     *
     * @param slicer
     *            切分者
     */
    void flush(S slicer);
}
