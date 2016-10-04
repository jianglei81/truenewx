package org.truenewx.data.query;

import java.util.Map.Entry;
import java.util.Set;

/**
 * 查询排序集
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public interface QueryOrders {

    boolean hasOrder();

    Boolean getOrder(String fieldName);

    Set<String> getOrderFieldNames();

    Iterable<Entry<String, Boolean>> getOrders();

}
