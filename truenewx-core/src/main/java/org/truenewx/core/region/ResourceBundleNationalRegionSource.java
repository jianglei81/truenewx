package org.truenewx.core.region;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.context.MessagesSource;
import org.truenewx.core.spring.context.ReloadableResourceBundleMessagesSource;

/**
 * 基于资源绑定属性文件的国家级区划来源实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ResourceBundleNationalRegionSource extends AbstractNationalRegionSource {
    /**
     * 消息集来源
     */
    private MessagesSource messagesSource;
    /**
     * 区域选项映射集解析器
     */
    private RegionMapParser parser;

    public void setParser(final RegionMapParser parser) {
        this.parser = parser;
    }

    @Override
    public void setNation(final String nation) {
        super.setNation(nation);
        // 设置好国家代号后，即可初始化国际化消息来源
        this.messagesSource = new ReloadableResourceBundleMessagesSource(
                StringUtils.join(RESOURCE_DIR, nation));
    }

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
        final Map<String, String> messages = this.messagesSource.getMessages(locale);
        final String nation = getNation();
        final String nationCaption = messages.get(nation);
        if (nationCaption != null) { // 取得到国家显示名才构建国家级区域选项
            final Region nationalRegion = new Region(nation, nationCaption);
            if (this.parser != null) {
                final Iterable<Region> subs = this.parser.parseAll(messages);

                final Map<String, Region> codeSubsMap = new HashMap<>();
                final Map<String, Region> captionSubsMap = new HashMap<>();
                for (final Region sub : subs) {
                    codeSubsMap.put(sub.getCode(), sub);
                    final StringBuffer caption = new StringBuffer(sub.getCaption());
                    Region parent = sub.getParent();
                    if (parent == null) { // 所有子选项中未指定父选项的才作为下一级子选项加入国家级选项中
                        nationalRegion.addSub(sub);
                    }
                    while (parent != null && !parent.getCode().equals(nation)) { // 不加国别名称
                        caption.insert(0, Strings.MINUS).insert(0, parent.getCaption());
                        parent = parent.getParent();
                    }
                    captionSubsMap.put(caption.toString(), sub);
                }
                this.localeCodeSubsMap.put(locale, codeSubsMap);
                this.localeCaptionSubsMap.put(locale, captionSubsMap);
            }
            this.localeNationalRegionMap.put(locale, nationalRegion);
            return nationalRegion;
        }
        return null;
    }
}
