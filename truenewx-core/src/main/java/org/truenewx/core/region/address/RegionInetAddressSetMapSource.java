package org.truenewx.core.region.address;

import java.util.Map;

import javax.annotation.Nullable;

import org.truenewx.core.net.InetAddressSet;

/**
 * 区划-网络地址集合的映射集来源
 *
 * @author jianglei
 * @version 1.0.0 2014年7月14日
 * @since JDK 1.8
 */
public interface RegionInetAddressSetMapSource {
    /**
     * 获取区划-网络地址集合的映射集
     *
     * @return 区划-网络地址集合的映射集
     */
    @Nullable
    Map<String, InetAddressSet> getMap();
}
