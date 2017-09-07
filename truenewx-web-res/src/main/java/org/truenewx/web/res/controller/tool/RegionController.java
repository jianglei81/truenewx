package org.truenewx.web.res.controller.tool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.truenewx.core.Strings;
import org.truenewx.core.region.Region;
import org.truenewx.core.region.RegionMapParser;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.rpc.server.annotation.RpcResult;
import org.truenewx.web.rpc.server.annotation.RpcResultFilter;

/**
 * 工具
 *
 * @author jianglei
 *
 */
@RpcController("RegionToolController")
@RequestMapping("/tool/region")
public class RegionController {

    @Autowired
    private RegionMapParser parser;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "/tool/region/index";
    }

    @RpcMethod(result = @RpcResult(filter = @RpcResultFilter(type = Region.class,
            includes = { "code", "caption", "level", "subs" })))
    public Iterable<Region> analyze(final String nation, final String source) {
        if (StringUtils.isBlank(source) || !source.startsWith("110000")) { // 如果提交的原始数据格式不正确，则返回null
            return null;
        }
        final Iterable<Region> regions = parse(nation, source);
        final List<Region> result = new ArrayList<>();
        for (final Region region : regions) {
            if (region.getParent() == null) { // 只加入顶级节点
                result.add(region);
            }
        }
        return result;
    }

    private Iterable<Region> parse(final String nation, final String source) {
        final Map<String, String> codeCaptionMap = new LinkedHashMap<>();
        try {
            final List<String> lines = IOUtils
                    .readLines(new ByteArrayInputStream(source.getBytes()));
            for (final String line : lines) {
                final String[] pair = line.replaceAll("　+", Strings.SPACE).split(Strings.SPACE, 2);
                final String code = nation + pair[0].trim();
                final String caption = pair[1].trim();
                codeCaptionMap.put(code, caption);
            }
        } catch (final IOException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return this.parser.parseAll(codeCaptionMap);
    }

}
