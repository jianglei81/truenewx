package org.truenewx.data.query;

import java.util.Map.Entry;
import java.util.Set;

/**
 * 查询排序
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface QueryOrder {

    boolean hasOrder();

    Boolean getOrder(String fieldName);

    Set<String> getOrderFieldNames();

    Iterable<Entry<String, Boolean>> getOrders();

}
