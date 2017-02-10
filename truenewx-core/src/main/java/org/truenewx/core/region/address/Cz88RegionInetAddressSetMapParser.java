package org.truenewx.core.region.address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.truenewx.core.Strings;
import org.truenewx.core.net.InetAddressRange;
import org.truenewx.core.net.InetAddressSet;
import org.truenewx.core.region.Region;
import org.truenewx.core.region.RegionSource;
import org.truenewx.core.util.ArrayUtil;
import org.truenewx.core.util.NetUtil;

/**
 * Cz88的区划-网络地址集合映射集解析器
 *
 * @author jianglei
 * @version 1.0.0 2014年7月14日
 * @since JDK 1.8
 */
public class Cz88RegionInetAddressSetMapParser implements RegionInetAddressSetMapParser {

    private RegionSource regionSource;
    private String defaultNation = "CN";

    private String[] removedProvinceSuffixes = new String[0];
    private String[] provinceSuffixes = new String[0];
    private String[] citySuffixes = new String[0];
    private String[] countySuffixes = new String[0];
    private Map<String, String> lineMapping = new HashMap<>();
    private Map<String, String> provinceMapping = new HashMap<>();
    private Map<String, String> cityMapping = new HashMap<>();
    private Map<String, String> countyMapping = new HashMap<>();

    public void setRegionSource(final RegionSource regionSource) {
        this.regionSource = regionSource;
    }

    public void setDefaultNation(final String defaultNation) {
        this.defaultNation = defaultNation;
    }

    public void setRemovedProvinceSuffixes(final String[] removedProvinceSuffixes) {
        this.removedProvinceSuffixes = removedProvinceSuffixes;
    }

    public void setRemovedProvinceSuffix(final String removedProvinceSuffix) {
        this.removedProvinceSuffixes = removedProvinceSuffix.split(Strings.COMMA);
    }

    public void setProvinceSuffixes(final String[] provinceSuffixes) {
        this.provinceSuffixes = provinceSuffixes;
    }

    public void setProvinceSuffix(final String provinceSuffix) {
        this.provinceSuffixes = provinceSuffix.split(Strings.COMMA);
    }

    public void setCitySuffixes(final String[] citySuffixes) {
        this.citySuffixes = citySuffixes;
    }

    public void setCitySuffix(final String citySuffix) {
        this.citySuffixes = citySuffix.split(Strings.COMMA);
    }

    public void setCountySuffixes(final String[] countySuffixes) {
        this.countySuffixes = countySuffixes;
    }

    public void setCountySuffix(final String countySuffix) {
        this.countySuffixes = countySuffix.split(Strings.COMMA);
    }

    public void setLineMapping(final Map<String, String> lineMapping) {
        this.lineMapping = lineMapping;
    }

    public void setProvinceMapping(final Map<String, String> provinceMapping) {
        this.provinceMapping = provinceMapping;
    }

    public void setCityMapping(final Map<String, String> cityMapping) {
        this.cityMapping = cityMapping;
    }

    public void setCountyMapping(final Map<String, String> countyMapping) {
        this.countyMapping = countyMapping;
    }

    @Override
    public Map<String, InetAddressSet> parse(final InputStream in, final Locale locale,
            final String encoding) throws IOException {
        final Map<String, InetAddressSet> result = new HashMap<>();
        final Logger logger = LoggerFactory.getLogger(getClass());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
        String line = reader.readLine();
        while (line != null) {
            line = transLine(line);
            try {
                parseLineTo(line, result, locale);
            } catch (final Exception e) { // 仅打印异常堆栈，不影响后续行的解析
                logger.debug(e.getMessage());
            }
            line = reader.readLine();
        }
        return result;
    }

    private String transLine(final String line) {
        if (StringUtils.isNotBlank(line)) {
            for (final Entry<String, String> entry : this.lineMapping.entrySet()) {
                final String translatedLine = line.replaceAll(entry.getKey(), entry.getValue());
                if (!line.equals(translatedLine)) { // 一旦有一个转换，则立即返回
                    return translatedLine;
                }
            }
        }
        return line;
    }

    private void parseLineTo(final String line, final Map<String, InetAddressSet> map,
            final Locale locale) {
        if (StringUtils.isNotBlank(line) && line.indexOf(Strings.DOT) > 0) { // 包括点号才包含有效的IPv4地址
            final String[] array = line.split(" +");
            if (array.length >= 3) {
                final Inet4Address begin = NetUtil.getInet4Address(array[0]);
                final Inet4Address end = NetUtil.getInet4Address(array[1]);
                if (begin != null && end != null) { // 有效的IP地址才考虑后续
                    final String regionCode = parseRegionCode(array[2], ArrayUtil.get(array, 3),
                            locale);
                    if (regionCode != null) {
                        InetAddressSet set = map.get(regionCode);
                        if (set == null) {
                            set = new InetAddressSet();
                            map.put(regionCode, set);
                        }
                        set.add(new InetAddressRange<>(begin, end));
                    }
                }
            }
        }
    }

    private String parseRegionCode(String caption, final String remark, final Locale locale) {
        String provinceCaption = null;
        for (final String suffix : this.provinceSuffixes) {
            int index = caption.indexOf(suffix);
            if (index >= 0) { // 匹配到省级名称后缀
                index += suffix.length(); // 定位到后缀后一位以便于截取
                provinceCaption = caption.substring(0, index);
                provinceCaption = transCaption(provinceCaption, this.provinceMapping);
                // 最后移除省份名称中需要移除的后缀
                for (final String removedSuffix : this.removedProvinceSuffixes) {
                    if (provinceCaption.endsWith(removedSuffix)) {
                        provinceCaption = provinceCaption.substring(0,
                                provinceCaption.length() - removedSuffix.length());
                        break;
                    }
                }
                caption = caption.substring(index); // 去掉省级名称部分以便于后续查找
                break;
            }
        }
        if (provinceCaption != null) { // 找到省级名称才可能有效
            String cityCaption = null; // 市级可以为空
            for (final String suffix : this.citySuffixes) {
                int index = caption.indexOf(suffix);
                if (index >= 0) {
                    index += suffix.length(); // 定位到后缀后一位以便于截取
                    cityCaption = caption.substring(0, index);
                    cityCaption = transCaption(cityCaption, this.cityMapping);
                    caption = caption.substring(index); // 去掉市级名称部分以便于后续查找
                    break;
                }
            }
            String countyCaption = null; // 县级可以为空
            if (cityCaption != null) { // 找到市级找县级才有意义
                for (final String suffix : this.countySuffixes) {
                    int index = caption.indexOf(suffix);
                    if (index >= 0) {
                        index += suffix.length(); // 定位到后缀后一位以便于截取
                        countyCaption = caption.substring(0, index);
                        countyCaption = transCaption(countyCaption, this.countyMapping);
                        break;
                    }
                }
            }
            if (countyCaption == null && remark != null) { // 未找到县级，则尝试从备注中转换获取
                if (cityCaption == null) { // 此时如果市级为空，则从备注中转换获取的为市级
                    for (final String suffix : this.citySuffixes) {
                        int index = remark.indexOf(suffix);
                        if (index >= 0) {
                            index += suffix.length(); // 定位到后缀后一位以便于截取
                            cityCaption = remark.substring(0, index);
                            cityCaption = transCaption(cityCaption, this.cityMapping);
                            break;
                        }
                    }
                } else { // 否则转换获取的为县级
                    for (final String suffix : this.countySuffixes) {
                        int index = remark.indexOf(suffix);
                        if (index >= 0) {
                            index += suffix.length(); // 定位到后缀后一位以便于截取
                            countyCaption = remark.substring(0, index);
                            countyCaption = transCaption(countyCaption, this.countyMapping);
                        }
                    }
                }
            }
            Region region = this.regionSource.getRegion(this.defaultNation,
                    provinceCaption, cityCaption, countyCaption, locale);
            // 如果无法取得区划选项，则尝试取上一级的区划选项
            if (region == null && countyCaption != null) {
                region = this.regionSource.getRegion(this.defaultNation,
                        provinceCaption, cityCaption, null, locale);
            }
            if (region == null && cityCaption != null) {
                region = this.regionSource.getRegion(this.defaultNation,
                        provinceCaption, null, null, locale);
            }
            if (region != null) {
                return region.getCode();
            }
        }
        return null;
    }

    private String transCaption(final String caption, final Map<String, String> mapping) {
        final String value = mapping.get(caption);
        return value == null ? caption : value;
    }
}
