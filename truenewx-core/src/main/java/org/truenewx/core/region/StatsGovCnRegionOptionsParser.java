package org.truenewx.core.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.truenewx.core.Strings;
import org.truenewx.core.util.StringUtil;

/**
 * 源于国家统计局数据的区划选项解析器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class StatsGovCnRegionOptionsParser implements RegionOptionsParser {
    /**
     * 要排除的显示名集合
     */
    private String[] excludedCaptions;

    private Comparator<Entry<String, String>> comparator = new Comparator<Map.Entry<String, String>>() {
        @Override
        public int compare(final Entry<String, String> entry1, final Entry<String, String> entry2) {
            return entry1.getKey().toString().compareTo(entry2.getKey().toString());
        }
    };

    public void setExcludedCaptions(final String[] excludedCaptions) {
        this.excludedCaptions = excludedCaptions;
    }

    public void setExcludedCaption(final String excludedCaption) {
        this.excludedCaptions = excludedCaption.split(Strings.COMMA);
    }

    @Override
    public Iterable<RegionOption> parseAll(final Map<String, String> codeCaptionMap) {
        // 取得排好序的条目集
        final Set<Entry<String, String>> entrySet = new TreeSet<>(this.comparator);
        entrySet.addAll(codeCaptionMap.entrySet());

        // 遍历排序后的条目集构建结果
        final Collection<RegionOption> result = new ArrayList<>();
        final Map<String, RegionOption> codeOptionMap = new HashMap<>();
        for (final Entry<String, String> entry : entrySet) {
            final String code = entry.getKey();
            if (code.length() == 8) { // 代码一定是8位的
                final String value = entry.getValue();
                final String caption;
                final String group;
                final int index = value.indexOf(Strings.LEFT_BRACKET);
                if (index >= 0) { // 如果有括号，则括号中的值为分组
                    caption = value.substring(0, index);
                    group = value.substring(index + 1,
                            value.length() - Strings.RIGHT_BRACKET.length());
                } else { // 没有括号，则没有分组，值即为显示名
                    caption = value;
                    group = null;
                }
                if (!StringUtil.wildcardMatchOneOf(caption, this.excludedCaptions)) { // 检查是否被排除
                    final RegionOption option;
                    if (code.endsWith("0000")) { // 0000结尾的为省级区划，省级区划不需要考虑父级选项，且所有省级区划均为有效
                        option = new RegionOption(code, caption, group);
                    } else { // 其它为市级或县级区划，需考虑父级选项
                        option = new RegionOption(code, caption, group);
                        final RegionOption parentOption = getParentOption(codeOptionMap, code);
                        if (parentOption != null) {
                            parentOption.addSub(option);
                        }
                    }
                    result.add(option);
                    codeOptionMap.put(option.getCode(), option);
                }
            }
        }
        return result;
    }

    /**
     * 从指定代号-选项映射集中找出指定代号的父级选项
     *
     * @param codeOptionMap
     *            代号-选项映射集
     * @param code
     *            代号
     * @return 父级选项
     */
    private RegionOption getParentOption(final Map<String, RegionOption> codeOptionMap,
            final String code) {
        if (code.length() <= 2) { // 代号长度小于等于2，则不可能有父级
            return null;
        }
        final String parentCode = getParentCode(code); // 父级代号
        final RegionOption parentOption = codeOptionMap.get(parentCode);
        if (parentOption != null) {
            return parentOption;
        }
        // 如果直接父级选项找不到，则再向上一级查找父级选项
        return getParentOption(codeOptionMap, parentCode);
    }

    /**
     * 获取指定代号的父级代号
     *
     * @param code
     *            代号
     * @return 父级代号
     */
    private String getParentCode(String code) {
        final int length = code.length(); // 应有的长度
        // 取非00结尾的前缀部分
        while (code.endsWith("00")) {
            code = code.substring(0, code.length() - 2);
        }
        // 再往前取一级
        code = code.substring(0, code.length() - 2);
        // 如果此时代号小于等于两位，则已经到达国家代号，返回国家代号作为父级代号
        if (code.length() <= 2) {
            return code;
        }
        // 后面补00直到长度为应有长度
        while (code.length() < length) {
            code += "00";
        }
        return code;
    }
}
