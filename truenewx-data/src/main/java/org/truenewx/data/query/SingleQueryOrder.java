package org.truenewx.data.query;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * 单字段查询排序
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SingleQueryOrder extends FieldOrder implements QueryOrder {

    private static final long serialVersionUID = 1512449230344606821L;

    public SingleQueryOrder(String name, boolean desc) {
        super(name, desc);
    }

    @Override
    public boolean hasOrder() {
        return StringUtils.isNotBlank(getName());
    }

    @Override
    public Boolean getOrder(String fieldName) {
        if (fieldName.equals(getName())) {
            return isDesc();
        }
        return null;
    }

    @Override
    public Iterable<String> getOrderFieldNames() {
        return Arrays.asList(getName());
    }

    @Override
    public Iterable<Entry<String, Boolean>> getOrders() {
        return Arrays.asList(new SimpleEntry<>(getName(), isDesc()));
    }

}
