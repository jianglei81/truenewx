package org.truenewx.web.res.controller.rpc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.truenewx.core.util.CollectionUtil;
import org.truenewx.core.util.DateUtil;
import org.truenewx.web.res.model.Image;
import org.truenewx.web.rpc.server.annotation.RpcArg;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.rpc.server.annotation.RpcResult;

/**
 * ApiTestController
 *
 * @author jianglei
 * @since JDK 1.8
 */
@RpcController(caption = "API测试", module = "测试")
public class ApiTestController {
    @RpcMethod(caption = "整数相加", args = { @RpcArg(name = "x", caption = "整数x"),
                    @RpcArg(name = "y", caption = "整数y") }, result = @RpcResult(caption = "x+y的值"))
    public int add(final int x, final int y) {
        return x + y;
    }

    @RpcMethod(logined = true)
    public long subtract(final long x, final long y) {
        return x - y;
    }

    @RpcMethod(lan = true)
    public float multiply(final float x, final float y) {
        return x * y;
    }

    @RpcMethod(logined = true, lan = true)
    public double divide(final double x, final double y) {
        return x / y;
    }

    @RpcMethod(caption = "从图片列表中获取指定下标的图片", args = {
                    @RpcArg(name = "list", caption = "图片列表", componentType = Image.class),
                    @RpcArg(name = "index", caption = "索引下标") })
    public Image getImageByIndex(final List<Image> list, final int index) {
        return CollectionUtil.get(list, index);
    }

    @RpcMethod(caption = "将指定关键字和图片转换为Map", args = { @RpcArg(name = "key", caption = "关键字"),
                    @RpcArg(name = "image", caption = "图片") }, result = @RpcResult(componentType = Image.class))
    public Map<String, Image> toMap(final String key, final Image image) {
        final Map<String, Image> map = new HashMap<>();
        map.put(key, image);
        return map;
    }

    @RpcMethod(caption = "为指定日期添加指定天数", args = { @RpcArg(name = "date", caption = "日期"),
                    @RpcArg(name = "days", caption = "添加的天数") }, result = @RpcResult(caption = "添加了天数后的新日期"))
    public Date addDays(final Date date, final int days) {
        return DateUtil.addDays(date, days);
    }
}
