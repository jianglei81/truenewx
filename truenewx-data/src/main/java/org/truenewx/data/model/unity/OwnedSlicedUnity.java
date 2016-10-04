package org.truenewx.data.model.unity;

import java.io.Serializable;

/**
 * 具有所属者的切分单体
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface OwnedSlicedUnity<K extends Serializable, S extends Serializable, O extends Serializable>
        extends SlicedUnity<K, S>, OwnedUnity<K, O> {

}
