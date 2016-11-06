package org.truenewx.core.region;

import java.util.Map;

/**
 * 区划选项映射集解析器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface RegionOptionsParser {
    /**
     * 从指定代号-显示名映射集中解析生成所有区划选项集，这些选项相互建立好了父子关联，但结果以平铺形式返回
     *
     * @param codeCaptionMap
     *            资源包
     * @return 平铺形式组合的所有区划选项集
     */
    Iterable<RegionOption> parseAll(Map<String, String> codeCaptionMap);
}
