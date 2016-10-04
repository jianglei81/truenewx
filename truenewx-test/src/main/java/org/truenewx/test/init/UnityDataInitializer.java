package org.truenewx.test.init;

import java.io.Serializable;

import org.truenewx.data.model.unity.Unity;
import org.truenewx.data.orm.dao.UnityDao;

/**
 * 单体数据初始化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class UnityDataInitializer<T extends Unity<K>, K extends Serializable>
        extends CodingDataInitializer<T> {

    @Override
    protected abstract UnityDao<T, K> getDao();

}
