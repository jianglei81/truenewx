package org.truenewx.data.model;

/**
 * 传输模型，用于在不同层次之间传递参数<br/>
 * 每一个传输模型都对应且仅对应于一个实体模型
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <T>
 *            实体类型
 */
public interface TransportModel<T extends Entity> extends Model {

}
