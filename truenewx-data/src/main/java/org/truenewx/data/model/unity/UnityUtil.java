package org.truenewx.data.model.unity;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 单体工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UnityUtil {

    private UnityUtil() {
    }

    /**
     * 获取指定单体集合的id集合
     *
     * @param unities 单体集合
     * @return id集合
     */
    public static <K extends Serializable> Set<K> getIdSet(Collection<? extends Unity<K>> unities) {
        Set<K> ids = new LinkedHashSet<>(); // 保持顺序
        for (Unity<K> unity : unities) {
            ids.add(unity.getId());
        }
        return ids;
    }

    /**
     * 判断指定单体集合中是否包含指定id的单体
     *
     * @param unities 单体集合
     * @param id      单体id
     * @return 指定单体集合中是否包含指定id的单体
     */
    public static <T extends Unity<K>, K extends Serializable> boolean containsId(
            Collection<T> unities, K id) {
        return indexOfId(unities, id) >= 0;
    }

    /**
     * 获取指定单体id在指定单体集合中的位置索引下标
     *
     * @param unities 单体集合
     * @param id      单体id
     * @return 指定单体id在指定单体集合中的位置索引下标，如果没找到则返回-1
     */
    public static <T extends Unity<K>, K extends Serializable> int indexOfId(Collection<T> unities,
            K id) {
        if (unities != null) {
            int i = 0;
            for (Unity<K> unity : unities) {
                if (unity.getId().equals(id)) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    /**
     * 将指定单体集合转换为以id为key，单体对象为value的Map映射集
     *
     * @param collection 单体集合
     * @return 单体映射集
     */
    public static <T extends Unity<K>, K extends Serializable> Map<K, T> toMap(
            Collection<T> collection) {
        if (collection == null) {
            return null;
        }
        Map<K, T> map = new HashMap<>();
        for (T unity : collection) {
            map.put(unity.getId(), unity);
        }
        return map;
    }

}
