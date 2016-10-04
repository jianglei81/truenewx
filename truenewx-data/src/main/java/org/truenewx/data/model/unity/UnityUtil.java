package org.truenewx.data.model.unity;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
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
     * @param unities
     *            单体集合
     * @return id集合
     */
    public static <K extends Serializable> Set<K> getIdSet(
            final Collection<? extends Unity<K>> unities) {
        final Set<K> ids = new LinkedHashSet<>(); // 保持顺序
        for (final Unity<K> unity : unities) {
            ids.add(unity.getId());
        }
        return ids;
    }

}
