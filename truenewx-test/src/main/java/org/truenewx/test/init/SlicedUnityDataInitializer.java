package org.truenewx.test.init;

import java.io.Serializable;

import org.truenewx.data.model.unity.SlicedUnity;
import org.truenewx.data.orm.dao.SlicedUnityDao;

/**
 * 切分单体数据初始化器
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            单体类型
 * @param <K>
 *            标识类型
 * @param <O>
 */
public abstract class SlicedUnityDataInitializer<T extends SlicedUnity<K, S>, K extends Serializable, S extends Serializable>
        extends CodingDataInitializer<T> {

    @Override
    protected abstract SlicedUnityDao<T, K, S> getDao();

}
