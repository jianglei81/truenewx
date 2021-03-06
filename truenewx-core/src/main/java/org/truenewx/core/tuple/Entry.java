package org.truenewx.core.tuple;

/**
 * 键值对条目
 * 
 * @author jianglei
 * @since JDK 1.8
 * @param <K>
 *            键类型
 * @param <V>
 *            值类型
 */
public interface Entry<K, V> {

    /**
     * 获取键
     * 
     * @return 键
     */
    public K getKey();

    /**
     * 获取值
     * 
     * @return 值
     */
    public V getValue();

}