package org.truenewx.core.region;

import java.util.Map;

/**
 * 行政区划映射集解析器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface RegionMapParser {
    /**
     * 从指定代号-显示名映射集中解析生成所有行政区划集，这些行政区划相互建立好了父子关联，但结果以平铺形式返回
     *
     * @param codeCaptionMap
     *            资源包
     * @return 平铺形式组合的行政区划集
     */
    Iterable<Region> parseAll(Map<String, String> codeCaptionMap);
}
