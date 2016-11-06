package org.truenewx.core.region;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.context.MessagesSource;
import org.truenewx.core.spring.context.ReloadableResourceBundleMessagesSource;

/**
 * 国家级区划选项解决器实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NationalRegionOptionSourceImpl implements NationalRegionOptionSource {
    /**
     * 资源文件目录
     */
    private static final String RESOURCE_DIR = "classpath:META-INF/region/";
    /**
     * 国家代号
     */
    private String nation;
    /**
     * 消息集来源
     */
    private MessagesSource messagesSource;
    /**
     * 区域选项映射集解析器
     */
    private RegionOptionsParser parser;
    /**
     * 显示区域-当前国家级选项的映射集
     */
    private Map<Locale, RegionOption> localeNationalOptionMap = new HashMap<>();
    /**
     * 显示区域-区划代号-区划选项的映射集
     */
    private Map<Locale, Map<String, RegionOption>> localeCodeSubsMap = new HashMap<>();
    /**
     * 显示区域-区划名称-区划选项的映射集
     */
    private Map<Locale, Map<String, RegionOption>> localeCaptionSubsMap = new HashMap<>();

    public void setNation(final String nation) {
        Assert.isTrue(nation.length() == RegionOptionSource.NATION_LENGTH,
                "The length of nation must be " + RegionOptionSource.NATION_LENGTH);
        this.nation = nation.toUpperCase();
        // 设置好国家代号后，即可初始化国际化消息来源
        this.messagesSource = new ReloadableResourceBundleMessagesSource(
                StringUtils.join(RESOURCE_DIR, this.nation));
    }

    public void setParser(final RegionOptionsParser parser) {
        this.parser = parser;
    }

    @Override
    public String getNation() {
        return this.nation;
    }

    @Override
    public RegionOption getNationalRegionOption(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        RegionOption regionOption = this.localeNationalOptionMap.get(locale);
        if (regionOption == null) {
            regionOption = buildNationalOption(locale);
        }
        return regionOption;
    }

    @Override
    @Nullable
    public RegionOption getSubRegionOption(final String code, @Nullable
    Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Map<String, RegionOption> codeSubsMap = this.localeCodeSubsMap.get(locale);
        if (codeSubsMap == null) {
            buildNationalOption(locale);
            codeSubsMap = this.localeCodeSubsMap.get(locale);
        }
        if (codeSubsMap != null) {
            return codeSubsMap.get(code);
        }
        return null;
    }

    @Override
    public RegionOption getSubRegionOption(final String provinceCaption, final String cityCaption,
            final String countyCaption, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Map<String, RegionOption> captionSubsMap = this.localeCaptionSubsMap.get(locale);
        if (captionSubsMap == null) {
            buildNationalOption(locale);
            captionSubsMap = this.localeCaptionSubsMap.get(locale);
        }
        if (captionSubsMap != null) {
            final StringBuffer caption = new StringBuffer(provinceCaption);
            if (cityCaption != null) {
                caption.append(Strings.MINUS).append(cityCaption);
                if (countyCaption != null) { // 市级名称不为空，县级名称才有效
                    caption.append(Strings.MINUS).append(countyCaption);
                }
            }
            return captionSubsMap.get(caption.toString());
        }
        return null;
    }

    /**
     * 构建指定显示区域的当前国家区划选项
     *
     * @param locale
     *            显示区域
     * @return 当前国家区划选项
     */
    @Nullable
    private RegionOption buildNationalOption(final Locale locale) {
        final Map<String, String> messages = this.messagesSource.getMessages(locale);
        final String nationCaption = messages.get(this.nation);
        if (nationCaption != null) { // 取得到国家显示名才构建国家级区域选项
            final RegionOption nationalOption = new RegionOption(this.nation, nationCaption);
            if (this.parser != null) {
                final Iterable<RegionOption> subs = this.parser.parseAll(messages);

                final Map<String, RegionOption> codeSubsMap = new HashMap<>();
                final Map<String, RegionOption> captionSubsMap = new HashMap<>();
                for (final RegionOption sub : subs) {
                    codeSubsMap.put(sub.getCode(), sub);
                    final StringBuffer caption = new StringBuffer(sub.getCaption());
                    RegionOption parent = sub.getParent();
                    if (parent == null) { // 所有子选项中未指定父选项的才作为下一级子选项加入国家级选项中
                        nationalOption.addSub(sub);
                    }
                    while (parent != null && !parent.getCode().equals(this.nation)) { // 不加国别名称
                        caption.insert(0, Strings.MINUS).insert(0, parent.getCaption());
                        parent = parent.getParent();
                    }
                    captionSubsMap.put(caption.toString(), sub);
                }
                this.localeCodeSubsMap.put(locale, codeSubsMap);
                this.localeCaptionSubsMap.put(locale, captionSubsMap);
            }
            this.localeNationalOptionMap.put(locale, nationalOption);
            return nationalOption;
        }
        return null;
    }
}
