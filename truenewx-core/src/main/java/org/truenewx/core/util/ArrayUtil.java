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
     * @param array 对象数组
     * @param index 索引下标
     * @return 指定索引下标处的对象
     */
    public static <T> T get(T[] array, int index) {
        if (array == null || index < 0 || array.length <= index) {
            return null;
        }
        return array[index];
    }

    /**
     * 从指定整型数组中获取指定索引下标处的整数，如果指定数组为空或长度不够，则返回指定默认值
     *
     * @param array        整型数组
     * @param index        索引下标
     * @param defaultValue 默认值
     * @return 指定索引下标处的整数值
     */
    public static int get(int[] array, int index, int defaultValue) {
        if (array == null || index < 0 || array.length <= index) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * 从指定长整型数组中获取指定索引下标处的长整数，如果指定数组为空或长度不够，则返回指定默认值
     *
     * @param array        长整型数组
     * @param index        索引下标
     * @param defaultValue 默认值
     * @return 指定索引下标处的长整数值
     */
    public static long get(long[] array, int index, long defaultValue) {
        if (array == null || index < 0 || array.length <= index) {
            return defaultValue;
        }
        return array[index];
    }

    /**
     * 将string数组转换成int数组
     *
     * @param stringArray 字符串数组
     * @return int数组
     */
    public static int[] toIntArray(String[] stringArray) {
        if (stringArray == null) {
            return null;
        }
        int length = stringArray.length;
        int[] intArray = new int[length];
        for (int i = 0; i < length; i++) {
            intArray[i] = MathUtil.parseInt(stringArray[i]);
        }
        return intArray;
    }

    public static Integer[] toIntegerArray(int[] intArray) {
        if (intArray == null) {
            return null;
        }
        int length = intArray.length;
        Integer[] integerArray = new Integer[length];
        for (int i = 0; i < length; i++) {
            integerArray[i] = intArray[i];
        }
        return integerArray;
    }

    public static Long[] toLongArray(long[] longArray) {
        if (longArray == null) {
            return null;
        }
        int length = longArray.length;
        Long[] result = new Long[length];
        for (int i = 0; i < length; i++) {
            result[i] = longArray[i];
        }
        return result;
    }

    public static Set<Integer> toSet(int[] array) {
        if (array == null) {
            return null;
        }
        Set<Integer> set = new HashSet<>();
        for (int i : array) {
            set.add(i);
        }
        return set;
    }

    public static Set<Long> toSet(long[] array) {
        if (array == null) {
            return null;
        }
        Set<Long> set = new HashSet<>();
        for (long l : array) {
            set.add(l);
        }
        return set;
    }

    public static <T> Set<T> toSet(T[] array) {
        if (array == null) {
            return null;
        }
        Set<T> set = new HashSet<>();
        for (T obj : array) {
            set.add(obj);
        }
        return set;
    }

    public static List<Integer> toList(int[] array) {
        if (array == null) {
            return null;
        }
        List<Integer> list = new ArrayList<>();
        for (int i : array) {
            list.add(i);
        }
        return list;
    }

    public static List<Long> toList(long[] array) {
        if (array == null) {
            return null;
        }
        List<Long> list = new ArrayList<>();
        for (long i : array) {
            list.add(i);
        }
        return list;
    }

    public static <T> List<T> toList(T[] array) {
        if (array == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        for (T obj : array) {
            list.add(obj);
        }
        return list;
    }

    public static void toLowerCase(String[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    array[i] = array[i].toLowerCase();
                }
            }
        }
    }

    public static void toUpperCase(String[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    array[i] = array[i].toUpperCase();
                }
            }
        }
    }

    public static void trim(String[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    array[i] = array[i].trim();
                }
            }
        }
    }

}
