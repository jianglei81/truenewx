package org.truenewx.data.model;

import java.io.Serializable;

import org.truenewx.core.model.Sliced;

/**
 * 切分的实体
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SlicedEntity<S extends Serializable> extends Entity, Sliced<S> {

}
