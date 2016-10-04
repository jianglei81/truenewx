package org.truenewx.core.tuple;

import java.util.Map;
import java.util.Objects;

/**
 * 简单键值对条目
 * 
 * @author jianglei
 * @since JDK 1.8
 * @param <K>
 *            键类型
 * @param <V>
 *            值类型
 */
public class SimpleEntry<K, V> implements Binate<K, V>, Entry<K, V>, Map.Entry<K, V>, Cloneable {
    private K key;
    private V value;

    public SimpleEntry() {
    }

    public SimpleEntry(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getLeft() {
        return getKey();
    }

    public void setLeft(final K left) {
        setKey(left);
    }

    @Override
    public V getRight() {
        return getValue();
    }

    public void setRight(final V right) {
        setValue(right);
    }

    @Override
    public K getKey() {
        return this.key;
    }

    public void setKey(final K key) {
        this.key = key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(final V value) {
        final V result = this.value;
        this.value = value;
        return result;
    }

    @Override
    public SimpleEntry<K, V> clone() {
        final SimpleEntry<K, V> entry = new SimpleEntry<K, V>();
        entry.key = this.key;
        entry.value = this.value;
        return entry;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SimpleEntry<?, ?> other = (SimpleEntry<?, ?>) obj;
        return Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode() + 19 * this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.key + "=" + this.value;
    }
}
