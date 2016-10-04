package org.truenewx.data.model;

/**
 * 提交模型，用于视图层向逻辑层提交数据
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface SubmitModel<T extends Entity> extends TransportModel<T> {

}
