package org.truenewx.data.model.unity;

import java.io.Serializable;

/**
 * 具有所属者的单体
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <K>
 *            标识类型
 * @param <O>
 *            所属者类型
 */
public interface OwnedUnity<K extends Serializable, O extends Serializable> extends Unity<K> {
    /**
     * @return 所有者
     */
    O getOwner();
}
