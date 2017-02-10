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
public class RegionSourceImpl implements RegionSource {
    /**
     * 国家级行政区划解析器映射集
     */
    private Map<String, NationalRegionSource> nationalSources = new LinkedHashMap<>();

    public void setNationalSources(final Iterable<NationalRegionSource> nationalRegionSources) {
        for (final NationalRegionSource nationalRegionSource : nationalRegionSources) {
            final String nation = nationalRegionSource.getNation();
            if (nation != null && nation.length() == RegionSource.NATION_LENGTH) { // 国家代号必须为固定长度
                this.nationalSources.put(nation.toUpperCase(), nationalRegionSource);
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
        if (region.length() >= RegionSource.NATION_LENGTH) {
            return region.substring(0, RegionSource.NATION_LENGTH).toUpperCase();
        }
        return null;
    }

    @Override
    @Nullable
    public Region getRegion(final String regionCode, @Nullable final Locale locale) {
        final String nation = getNation(regionCode);
        if (nation != null) {
            final NationalRegionSource nationalOptionSource = this.nationalSources.get(nation);
            if (nationalOptionSource != null) {
                if (nation.equals(regionCode)) { // 指定区划即为国家，直接取国家区划选项
                    return nationalOptionSource.getNationalRegion(locale);
                } else { // 否则从子孙区划中查找
                    return nationalOptionSource.getSubRegion(regionCode, locale);
                }
            }
        }
        return null;
    }

    @Override
    public Region getRegion(final String nation, final String provinceCaption,
            final String cityCaption, final String countyCaption, final Locale locale) {
        final NationalRegionSource nationalOptionSource = this.nationalSources.get(nation);
        if (nationalOptionSource != null) {
            if (provinceCaption == null) { // 如果未指定省份名称，则直接取国家区划选项
                return nationalOptionSource.getNationalRegion(locale);
            } else { // 否则从子孙区划中查找
                return nationalOptionSource.getSubRegion(provinceCaption, cityCaption,
                        countyCaption, locale);
            }
        }
        return null;
    }

    @Override
    public Collection<Region> getNationalRegions(final Locale locale) {
        final Collection<Region> result = new ArrayList<>();
        for (final NationalRegionSource nationalResolver : this.nationalSources.values()) {
            result.add(nationalResolver.getNationalRegion(locale));
        }
        return result;
    }

    @Override
    public Region getNationalRegion(final String nation, final Locale locale) {
        final NationalRegionSource source = this.nationalSources.get(nation);
        if (source != null) {
            return source.getNationalRegion(locale);
        }
        return null;
    }
}
