package org.truenewx.test.init;

import java.io.Serializable;

import org.truenewx.data.model.relation.SlicedRelation;
import org.truenewx.data.orm.dao.SlicedRelationDao;

/**
 * 切分关系数据初始化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class SlicedRelationDataInitializer<T extends SlicedRelation<L, R, S>, L extends Serializable, R extends Serializable, S extends Serializable>
        extends CodingDataInitializer<T> {

    @Override
    protected abstract SlicedRelationDao<T, L, R, S> getDao();

}
