package org.truenewx.data.finder;

import java.util.List;
import java.util.Map;

/**
 * 实体查找器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface EntityFinder<T> {

    /**
     * 根据指定字段参数映射集查找单体清单
     *
     * @param params
     *            字段参数映射集
     * @param fuzzyNames
     *            进行模糊查询的字段参数名
     * @return 查询结果单体清单
     */
    List<T> find(Map<String, ?> params, String... fuzzyNames);

}
