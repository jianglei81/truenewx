package org.truenewx.data.model.unity;

import java.io.Serializable;

import org.truenewx.data.model.UnitaryEntity;

/**
 * 单体，用id作为标识属性的实体
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <K>
 *            标识类型
 */
public interface Unity<K extends Serializable> extends UnitaryEntity<K> {
    /**
     * 获取标识
     *
     * @return 标识，唯一表示一个单体
     */
    K getId();
}
