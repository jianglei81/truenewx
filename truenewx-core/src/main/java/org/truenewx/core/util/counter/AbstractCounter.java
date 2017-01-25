package org.truenewx.core.util.counter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 抽象计数器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractCounter<K> implements Counter<K> {

    private Map<K, Integer> map;

    protected AbstractCounter(final Map<K, Integer> map) {
        this.map = map;
    }

    @Override
    public synchronized int add(final K key, final int step) {
        Integer count = this.map.get(key);
        if (count == null) {
            count = 0;
        }
        count += step;
        this.map.put(key, count);
        return count;
    }

    @Override
    public Integer remove(final K key) {
        return this.map.remove(key);
    }

    @Override
    public Integer count(final K key) {
        return this.map.get(key);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @Override
    public Set<Entry<K, Integer>> entrySet() {
        return this.map.entrySet();
    }

}
