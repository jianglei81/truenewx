package org.truenewx.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数组工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ArrayUtil {

    private ArrayUtil() {
    }

    /**
     * 从指定对象数组中获取指定索引下标处的对象，如果指定数组为空或长度不够，则返回null
     *
     * @param array
     *            对象数组
     * @param index
     *            索引下标
     * @return 指定索引下标处的对象
     */
    public static <T> T get(final T[] array, final int index) {
        if (array == null || index < 0 || array.length <= index) {
            return null;
        }
        return array[index];
    }

    /**
     * 从指定整型数组中获取指定索引下标处的整数，如果指定数组为空或长度不够，则返回指定默认值
     *
     * @param array
     *            整型数组
     * @param index
     *            索引下标
     * @param defaultValue
     *            默认值
     * @return 指定索引下标处的整数值
     */
    public static int get(final int[] array, final int index, final int defaultValue) {
        if (array == null || index < 0 || array.length <= index) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * 从指定长整型数组中获取指定索引下标处的长整数，如果指定数组为空或长度不够，则返回指定默认值
     *
     * @param array
     *            长整型数组
     * @param index
     *            索引下标
     * @param defaultValue
     *            默认值
     * @return 指定索引下标处的长整数值
     */
    public static long get(final long[] array, final int index, final long defaultValue) {
        if (array == null || index < 0 || array.length <= index) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * 将string数组转换成int数组
     *
     * @param stringArray
     *            字符串数组
     * @return int数组
     */
    public static int[] toIntArray(final String[] stringArray) {
        if (stringArray == null) {
            return null;
        }
        final int len = stringArray.length;
        final int[] intArray = new int[len];
        for (int i = 0; i < len; i++) {
            intArray[i] = MathUtil.parseInt(stringArray[i]);
        }
        return intArray;
    }

    public static Set<Integer> toSet(final int[] array) {
        if (array == null) {
            return null;
        }
        final Set<Integer> set = new HashSet<>();
        for (final int i : array) {
            set.add(i);
        }
        return set;
    }

    public static Set<Long> toSet(final long[] array) {
        if (array == null) {
            return null;
        }
        final Set<Long> set = new HashSet<>();
        for (final long l : array) {
            set.add(l);
        }
        return set;
    }

    public static <T> Set<T> toSet(final T[] array) {
        if (array == null) {
            return null;
        }
        final Set<T> set = new HashSet<>();
        for (final T obj : array) {
            set.add(obj);
        }
        return set;
    }

    public static List<Integer> toList(final int[] array) {
        if (array == null) {
            return null;
        }
        final List<Integer> list = new ArrayList<>();
        for (final int i : array) {
            list.add(i);
        }
        return list;
    }

    public static List<Long> toList(final long[] array) {
        if (array == null) {
            return null;
        }
        final List<Long> list = new ArrayList<>();
        for (final long i : array) {
            list.add(i);
        }
        return list;
    }

    public static <T> List<T> toList(final T[] array) {
        if (array == null) {
            return null;
        }
        final List<T> list = new ArrayList<>();
        for (final T obj : array) {
            list.add(obj);
        }
        return list;
    }

    public static void toLowerCase(final String[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    array[i] = array[i].toLowerCase();
                }
            }
        }
    }

    public static void toUpperCase(final String[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    array[i] = array[i].toUpperCase();
                }
            }
        }
    }

    public static void trim(final String[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    array[i] = array[i].trim();
                }
            }
        }
    }

}
