package org.truenewx.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * 集合工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class CollectionUtil {

    private CollectionUtil() {
    }

    /**
     * 获取指定可迭代对象的指定索引下标位置处的元素
     *
     * @param iterable
     *            指定可迭代对象，可为null
     * @param index
     *            索引下标，可超出可迭代对象中的元素个数，超出时返回null
     * @return 元素
     */
    @Nullable
    public static <T> T get(@Nullable final Iterable<T> iterable, final int index) {
        if (iterable != null && index >= 0) {
            if (iterable instanceof List) {
                final List<T> list = (List<T>) iterable;
                if (index < list.size()) {
                    return list.get(index);
                }
                return null;
            } else {
                if (iterable instanceof Collection) {
                    final Collection<T> collection = (Collection<T>) iterable;
                    if (index >= collection.size()) {
                        return null;
                    }
                }
                int i = 0;
                for (final T obj : iterable) {
                    if (i++ == index) {
                        return obj;
                    }
                }
                return null;
            }
        }
        return null;
    }

    /**
     * 获取指定集合的大小
     *
     * @param iterable
     *            集合
     * @return 集合的大小
     */
    public static int size(final Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).size();
        } else if (iterable instanceof Map) {
            return ((Map<?, ?>) iterable).size();
        } else {
            int size = 0;
            for (final Iterator<?> iterator = iterable.iterator(); iterator.hasNext();) {
                size++;
            }
            return size;
        }
    }

    /**
     * 判断指定集合是否包含指定元素
     *
     * @param iterable
     *            集合
     * @param element
     *            元素
     * @return 指定集合是否包含指定元素
     */
    public static <T> boolean contains(final Iterable<T> iterable, final T element) {
        for (final T e : iterable) {
            if (Objects.equals(e, element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将指定数据中的所有元素添加到指定集合中
     *
     * @param collection
     *            集合
     * @param array
     *            数组
     */
    public static <T> void addAll(final Collection<T> collection, final T[] array) {
        if (array != null) {
            for (final T e : array) {
                collection.add(e);
            }
        }
    }

    /**
     * 将Key为String的map转为Key为Integer的map
     *
     * @param map
     * @param minKey
     * @return
     *
     * @author jianglei
     */
    public static Map<Integer, String> toIntegerKeyMap(final Map<String, String> map,
            final int minKey) {
        final Map<Integer, String> newMap = new HashMap<>();
        for (final String key : map.keySet()) {
            final int newKey = MathUtil.parseInt(key, minKey - 1);
            if (newKey >= minKey) {
                newMap.put(newKey, map.get(key));
            }
        }
        return newMap;
    }

    /**
     * 将指定整数对象集合转换为基本整数数组
     *
     * @param collection
     *            集合
     * @return 基本长整数数组
     */
    public static int[] toIntArray(final Collection<Integer> collection) {
        if (collection == null) {
            return null;
        }
        final int[] array = new int[collection.size()];
        int i = 0;
        for (final Integer value : collection) {
            array[i++] = value;
        }
        return array;
    }

    /**
     * 将指定长整数对象集合转换为基本长整数数组
     *
     * @param collection
     *            集合
     * @return 基本长整数数组
     */
    public static long[] toLongArray(final Collection<Long> collection) {
        if (collection == null) {
            return null;
        }
        final long[] array = new long[collection.size()];
        int i = 0;
        for (final Long value : collection) {
            array[i++] = value;
        }
        return array;
    }

    /**
     * 将指定枚举集合转换为key为枚举名称，value为枚举常量的映射集
     * 
     * @param collection
     *            枚举集合
     * @return 枚举映射集
     */
    public static <T extends Enum<T>> Map<String, T> toMap(final Collection<T> collection) {
        if (collection == null) {
            return null;
        }
        final Map<String, T> map = new HashMap<>();
        for (final T constant : collection) {
            map.put(constant.name(), constant);
        }
        return map;
    }
}
