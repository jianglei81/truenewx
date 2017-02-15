package org.truenewx.core.region;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.truenewx.core.util.IOUtil;
import org.truenewx.core.util.JsonUtil;

/**
 * 基于JSON文件的国家级区划来源实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class JsonFileNationalRegionSource extends AbstractNationalRegionSource {
    /**
     * 构建指定显示区域的当前国家行政区划
     *
     * @param locale
     *            显示区域
     * @return 当前国家行政区划
     */
    @Override
    @Nullable
    protected Region buildNationalRegion(final Locale locale) {
        final Resource resource = IOUtil.findI18nResource(RESOURCE_DIR + getNation(), locale,
                "json");
        if (resource != null) {
            try {
                final String json = IOUtils.toString(resource.getInputStream());
                if (StringUtils.isNotBlank(json)) {
                    final MutableRegion nationalRegion = JsonUtil.json2Bean(json,
                            MutableRegion.class);
                    this.localeNationalRegionMap.put(locale, nationalRegion);
                    putLocaleSubsMap(locale, nationalRegion);
                    return nationalRegion;
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void putLocaleSubsMap(final Locale locale, final Region region) {
        Map<String, Region> codeSubMap = this.localeCodeSubsMap.get(locale);
        if (codeSubMap == null) {
            codeSubMap = new HashMap<>();
            this.localeCodeSubsMap.put(locale, codeSubMap);
        }

        Map<String, Region> captionSubMap = this.localeCaptionSubsMap.get(locale);
        if (captionSubMap == null) {
            captionSubMap = new HashMap<>();
            this.localeCaptionSubsMap.put(locale, captionSubMap);
        }

        final Collection<Region> subs = region.getSubs();
        if (subs != null) {
            for (final Region sub : subs) {
                codeSubMap.put(sub.getCode(), sub);
                captionSubMap.put(sub.getCaption(), sub);
                putLocaleSubsMap(locale, sub);
            }
        }
    }
}
