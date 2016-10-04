package org.truenewx.test.init;

import java.io.Serializable;

import org.truenewx.data.model.relation.Relation;
import org.truenewx.data.orm.dao.RelationDao;

/**
 * 关系数据初始化器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class RelationDataInitializer<T extends Relation<L, R>, L extends Serializable, R extends Serializable>
        extends CodingDataInitializer<T> {

    @Override
    protected abstract RelationDao<T, L, R> getDao();
}
