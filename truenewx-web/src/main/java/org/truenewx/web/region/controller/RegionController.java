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
import org.truenewx.core.region.Region;
import org.truenewx.core.region.RegionSource;
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
@RpcController("regionController")
public class RegionController {
    @Autowired
    private RegionSource regionSource;

    @RpcMethod(logined = false, result = @RpcResult(filter = @RpcResultFilter(type = Region.class, includes = {
            "code", "caption", "subs", "includingGrandSub" })))
    public Map<String, Region> getAll() {
        final Locale locale = SpringWebContext.getLocale();
        final Map<String, Region> result = new LinkedHashMap<>();
        final Collection<Region> nationalOptions = this.regionSource.getNationalRegions(locale);
        for (final Region nationalOption : nationalOptions) {
            result.put(nationalOption.getCode(), nationalOption);
        }
        return result;
    }

    @RpcMethod(logined = false, result = @RpcResult(filter = @RpcResultFilter(type = Region.class, includes = {
            "code", "caption", "subs", "includingGrandSub" })))
    public Map<String, Region> getLimits(final String[] limits) {
        if (ArrayUtils.isEmpty(limits)) {
            return null;
        }
        final List<Region> limitRegions = new ArrayList<>();
        final Locale locale = SpringWebContext.getLocale();
        for (final String limit : limits) {
            limitRegions.add(this.regionSource.getRegion(limit, locale));
        }
        final Map<String, Region> result = new HashMap<>();
        for (final Region region : limitRegions) {
            transLimitRegions(result, region);
        }
        return result;
    }

    private void transLimitRegions(final Map<String, Region> limitRegions, final Region region) {
        final List<Region> link = region.getLinkFromTop();
        Region originalRegion = link.get(0); // 至少有一个
        Region cloneOption = limitRegions.get(originalRegion.getCode());
        if (cloneOption == null) { // 没有顶级选项，则克隆一个加入
            cloneOption = originalRegion.clone(false);
            limitRegions.put(cloneOption.getCode(), cloneOption);
        }
        for (int i = 1; i < link.size(); i++) {
            originalRegion = link.get(i);
            Region sub = cloneOption.findSubByCode(originalRegion.getCode());
            if (sub == null) {
                sub = originalRegion.clone(false);
                cloneOption.addSub(sub);
            }
            cloneOption = sub;
        }
    }

    @RpcMethod(logined = false, result = @RpcResult(filter = @RpcResultFilter(type = Region.class, includes = {
            "code", "caption", "level", "subs" })))
    public Region getRegion(final String code) {
        final Locale locale = SpringWebContext.getLocale();
        return this.regionSource.getRegion(code, locale);
    }

    /**
     * 获取指定区划的各级父级代号集合，顺序依次为国家、省、市，指定区划为市级区划时结果为国家、省，指定区划为省时结果为国家
     *
     * @param region
     *            区划代号
     * @return 各级父级代号集合
     */
    @RpcMethod(logined = false)
    public Iterable<String> getParentCodes(final String region) {
        final List<String> codes = new ArrayList<>();
        final Locale locale = SpringWebContext.getLocale();
        final Region option = this.regionSource.getRegion(region, locale);
        if (option != null) {
            Region parent = option.getParent();
            while (parent != null) {
                codes.add(0, parent.getCode());
                parent = parent.getParent();
            }
        }
        return codes;
    }
}
