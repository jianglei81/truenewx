package org.truenewx.data.query;

import java.util.LinkedHashMap;
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
    public final boolean hasOrder() {
        return this.orders != null && !this.orders.isEmpty();
    }

    @Override
    public Set<String> getOrderFieldNames() {
        return this.orders == null ? null : this.orders.keySet();
    }

    @Override
    public final Iterable<Entry<String, Boolean>> getOrders() {
        return this.orders == null ? null : this.orders.entrySet();
    }

    public final String getOrderString() {
        final StringBuffer orderString = new StringBuffer();
        if (this.orders != null) {
            for (final Entry<String, Boolean> entry : this.orders.entrySet()) {
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

    public final void setOrderString(final String orderString) {
        initOrderMap(true);
        final Map<String, Boolean> orders = parseOrders(orderString);
        if (orders != null) {
            this.orders.putAll(orders);
        }
    }

    public final void setOrders(final Map<String, Boolean> orders) {
        this.orders = orders;
    }

    private void initOrderMap(final boolean clear) {
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
        final String[] entries = orderString.split(",");
        for (final String entry : entries) {
            final String[] values = entry.split(" ");
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
    public final void retainAllOrder(final QueryOrder order) {
        // 忽略自己覆盖自己
        if (this != order) {
            initOrderMap(true);
            if (order != null) {
                final Iterable<Entry<String, Boolean>> orders = order.getOrders();
                if (orders != null) {
                    for (final Entry<String, Boolean> entry : orders) {
                        this.orders.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    public final void setOrder(final String fieldName, final Boolean desc) {
        initOrderMap(false);
        if (desc == null) {
            this.orders.remove(fieldName);
        } else {
            this.orders.put(fieldName, desc);
        }
    }

    @Override
    public Boolean getOrder(final String fieldName) {
        return this.orders.get(fieldName);
    }
}
