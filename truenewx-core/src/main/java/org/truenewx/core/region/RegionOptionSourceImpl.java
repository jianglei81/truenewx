package org.truenewx.core.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * 行政区划来源实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RegionOptionSourceImpl implements RegionOptionSource {
    /**
     * 国家级行政区划解析器映射集
     */
    private Map<String, NationalRegionOptionSource> nationalOptionSources = new LinkedHashMap<>();

    public void setNationalOptionSources(
            final Iterable<NationalRegionOptionSource> nationalOptionSources) {
        for (final NationalRegionOptionSource nationalOptionSource : nationalOptionSources) {
            final String nation = nationalOptionSource.getNation();
            if (nation != null && nation.length() == RegionOptionSource.NATION_LENGTH) { // 国家代号必须为固定长度
                this.nationalOptionSources.put(nation.toUpperCase(), nationalOptionSource);
            }
        }
    }

    /**
     * 从区划代号中获取国家代号，如果区划代号不合法，则返回null
     *
     * @param region
     *            区划代号
     * @return 国家代号
     */
    private String getNation(final String region) {
        if (region.length() >= RegionOptionSource.NATION_LENGTH) {
            return region.substring(0, RegionOptionSource.NATION_LENGTH).toUpperCase();
        }
        return null;
    }

    @Override
    @Nullable
    public RegionOption getRegionOption(final String region, @Nullable final Locale locale) {
        final String nation = getNation(region);
        if (nation != null) {
            final NationalRegionOptionSource nationalOptionSource = this.nationalOptionSources
                    .get(nation);
            if (nationalOptionSource != null) {
                if (nation.equals(region)) { // 指定区划即为国家，直接取国家区划选项
                    return nationalOptionSource.getNationalRegionOption(locale);
                } else { // 否则从子孙区划中查找
                    return nationalOptionSource.getSubRegionOption(region, locale);
                }
            }
        }
        return null;
    }

    @Override
    public RegionOption getRegionOption(final String nation, final String provinceCaption,
            final String cityCaption, final String countyCaption, final Locale locale) {
        final NationalRegionOptionSource nationalOptionSource = this.nationalOptionSources
                .get(nation);
        if (nationalOptionSource != null) {
            if (provinceCaption == null) { // 如果未指定省份名称，则直接取国家区划选项
                return nationalOptionSource.getNationalRegionOption(locale);
            } else { // 否则从子孙区划中查找
                return nationalOptionSource.getSubRegionOption(provinceCaption, cityCaption,
                        countyCaption, locale);
            }
        }
        return null;
    }

    @Override
    public Collection<RegionOption> getNationalRegionOptions(final Locale locale) {
        final Collection<RegionOption> result = new ArrayList<>();
        for (final NationalRegionOptionSource nationalResolver : this.nationalOptionSources
                .values()) {
            result.add(nationalResolver.getNationalRegionOption(locale));
        }
        return result;
    }

    @Override
    public RegionOption getNationalRegionOption(final String nation, final Locale locale) {
        final NationalRegionOptionSource source = this.nationalOptionSources.get(nation);
        if (source != null) {
            return source.getNationalRegionOption(locale);
        }
        return null;
    }
}
