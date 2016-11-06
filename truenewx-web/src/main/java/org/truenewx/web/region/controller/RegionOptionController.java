package org.truenewx.web.region.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.core.region.RegionOption;
import org.truenewx.core.region.RegionOptionSource;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.rpc.server.annotation.RpcResult;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;
import org.truenewx.web.spring.context.SpringWebContext;

/**
 * 行政区划选项控制器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@RpcController("regionOptionController")
public class RegionOptionController {
    @Autowired
    private RegionOptionSource regionOptionSource;

    @RpcMethod(result = @RpcResult(filter = @RpcResultFilter(type = RegionOption.class,
            includes = { "code", "caption", "subs", "includingGrandSub" })))
    public Map<String, RegionOption> getAll() {
        final Locale locale = SpringWebContext.getLocale();
        final Map<String, RegionOption> result = new LinkedHashMap<>();
        final Collection<RegionOption> nationalOptions = this.regionOptionSource
                .getNationalRegionOptions(locale);
        for (final RegionOption nationalOption : nationalOptions) {
            result.put(nationalOption.getCode(), nationalOption);
        }
        return result;
    }

    @RpcMethod(result = @RpcResult(filter = @RpcResultFilter(type = RegionOption.class,
            includes = { "code", "caption", "subs", "includingGrandSub" })))
    public Map<String, RegionOption> getLimits(final String[] limits) {
        if (ArrayUtils.isEmpty(limits)) {
            return null;
        }
        final List<RegionOption> limitRegions = new ArrayList<>();
        final Locale locale = SpringWebContext.getLocale();
        for (final String limit : limits) {
            limitRegions.add(this.regionOptionSource.getRegionOption(limit, locale));
        }
        final Map<String, RegionOption> result = new HashMap<>();
        for (final RegionOption regionOption : limitRegions) {
            transLimitRegions(result, regionOption);
        }
        return result;
    }

    private void transLimitRegions(final Map<String, RegionOption> limitRegions,
            final RegionOption region) {
        final List<RegionOption> link = region.getLinkFromTop();
        RegionOption originalOption = link.get(0); // 至少有一个
        RegionOption cloneOption = limitRegions.get(originalOption.getCode());
        if (cloneOption == null) { // 没有顶级选项，则克隆一个加入
            cloneOption = originalOption.clone(false);
            limitRegions.put(cloneOption.getCode(), cloneOption);
        }
        for (int i = 1; i < link.size(); i++) {
            originalOption = link.get(i);
            RegionOption sub = cloneOption.getSubByCode(originalOption.getCode());
            if (sub == null) {
                sub = originalOption.clone(false);
                cloneOption.addSub(sub);
            }
            cloneOption = sub;
        }
    }

    @RpcMethod(result = @RpcResult(filter = @RpcResultFilter(type = RegionOption.class,
            includes = { "code", "caption", "parentCode", "subs" })))
    public RegionOption getNationalRegionOption(final String nation) {
        final Locale locale = SpringWebContext.getLocale();
        return this.regionOptionSource.getNationalRegionOption(nation, locale);
    }

    /**
     * 获取指定区划的各级父级代号集合，顺序依次为国家、省、市，指定区划为市级区划时结果为国家、省，指定区划为省时结果为国家
     *
     * @param region
     *            区划代号
     * @return 各级父级代号集合
     */
    @RpcMethod
    public Iterable<String> getParentCodes(final String region) {
        final List<String> codes = new ArrayList<>();
        final Locale locale = SpringWebContext.getLocale();
        final RegionOption option = this.regionOptionSource.getRegionOption(region, locale);
        if (option != null) {
            RegionOption parent = option.getParent();
            while (parent != null) {
                codes.add(0, parent.getCode());
                parent = parent.getParent();
            }
        }
        return codes;

    }
}
