package org.truenewx.data.model;

/**
 * 可克隆后存储到会话中
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface CloneableForSession<T> {

    /**
     * 克隆一个可存储到会话中的实例
     *
     * @return 可存储到会话中的克隆实例
     */
    T cloneForSession();

}
