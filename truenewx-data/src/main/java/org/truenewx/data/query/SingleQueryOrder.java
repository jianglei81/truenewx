package org.truenewx.data.query;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.truenewx.core.tuple.SimpleEntry;

/**
 * 单字段查询排序
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class SingleQueryOrder implements QueryOrder {

    private String fieldName;
    private boolean desc;

    public SingleQueryOrder(final String fieldName, final boolean desc) {
        Assert.isTrue(StringUtils.isNotBlank(fieldName), "fieldName must be not blank");
        this.fieldName = fieldName;
        this.desc = desc;
    }

    @Override
    public boolean hasOrder() {
        return true;
    }

    @Override
    public Boolean getOrder(final String fieldName) {
        if (this.fieldName.equals(fieldName)) {
            return this.desc;
        }
        return null;
    }

    @Override
    public Set<String> getOrderFieldNames() {
        final Set<String> fieldNames = new HashSet<>();
        fieldNames.add(this.fieldName);
        return fieldNames;
    }

    @Override
    public Iterable<Entry<String, Boolean>> getOrders() {
        final Set<Entry<String, Boolean>> entrySet = new HashSet<>();
        entrySet.add(new SimpleEntry<>(this.fieldName, this.desc));
        return entrySet;
    }

}
