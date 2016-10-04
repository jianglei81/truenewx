package org.truenewx.data.model.unity;

import java.io.Serializable;

import org.truenewx.data.model.SlicedEntity;

/**
 * 切分的单体
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <K>
 *            标识类型
 * @param <P>
 *            切分者类型
 */
public interface SlicedUnity<K extends Serializable, S extends Serializable>
                extends Unity<K>, SlicedEntity<S> {

}
