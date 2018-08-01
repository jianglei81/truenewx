package org.truenewx.data.query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;

/**
 * 多字段查询排序
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MultiQueryOrder implements QueryOrder {
    /**
     * 字段-是否倒序(desc)映射集
     */
    private Map<String, Boolean> orders;

    @Override
    public boolean hasOrder() {
        return this.orders != null && !this.orders.isEmpty();
    }

    @Override
    public Set<String> getOrderFieldNames() {
        return this.orders == null ? null : this.orders.keySet();
    }

    @Override
    public Iterable<Entry<String, Boolean>> getOrders() {
        return this.orders == null ? null : this.orders.entrySet();
    }

    public String getOrderString() {
        StringBuffer orderString = new StringBuffer();
        if (this.orders != null) {
            for (Entry<String, Boolean> entry : this.orders.entrySet()) {
                orderString.append(Strings.COMMA).append(entry.getKey());
                if (entry.getValue() == Boolean.TRUE) {
                    orderString.append(" desc");
                }
            }
        }
        if (orderString.length() > 0) {
            orderString.delete(0, Strings.COMMA.length()); // 去掉第一个逗号
        }
        return orderString.toString();
    }

    public void setOrderString(String orderString) {
        initOrderMap(true);
        Map<String, Boolean> orders = parseOrders(orderString);
        if (orders != null) {
            this.orders.putAll(orders);
        }
    }

    public void setOrders(List<Entry<String, Boolean>> orders) {
        initOrderMap(true);
        if (orders != null) {
            for (Entry<String, Boolean> entry : orders) {
                this.orders.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void initOrderMap(boolean clear) {
        if (this.orders == null) {
            this.orders = new LinkedHashMap<>();
        } else if (clear) {
            this.orders.clear();
        }
    }

    private Map<String, Boolean> parseOrders(String orderString) {
        orderString = orderString.trim();
        if (StringUtils.isEmpty(orderString)) {
            return null;
        }
        initOrderMap(true);
        String[] entries = orderString.split(",");
        for (String entry : entries) {
            String[] values = entry.split(" ");
            if (values.length == 1) {
                this.orders.put(values[0], false);
            } else if (values.length == 2) {
                if ("desc".equalsIgnoreCase(values[1])) {
                    this.orders.put(values[0], true);
                } else if ("asc".equalsIgnoreCase(values[1])) {
                    this.orders.put(values[0], false);
                }
            }
        }
        return this.orders;
    }

    /**
     * 只保留指定可排序对象中的排序
     */
    public void retainAllOrder(QueryOrder order) {
        // 忽略自己覆盖自己
        if (this != order) {
            initOrderMap(true);
            if (order != null) {
                Iterable<Entry<String, Boolean>> orders = order.getOrders();
                if (orders != null) {
                    for (Entry<String, Boolean> entry : orders) {
                        this.orders.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    public void setOrder(String fieldName, Boolean desc) {
        initOrderMap(false);
        if (desc == null) {
            this.orders.remove(fieldName);
        } else {
            this.orders.put(fieldName, desc);
        }
    }

    @Override
    public Boolean getOrder(String fieldName) {
        return this.orders.get(fieldName);
    }

    public Boolean rename(String oldFieldName, String newFieldName) {
        Boolean value = this.orders.remove(oldFieldName);
        if (value != null) {
            this.orders.put(newFieldName, value);
        }
        return value;
    }

}
