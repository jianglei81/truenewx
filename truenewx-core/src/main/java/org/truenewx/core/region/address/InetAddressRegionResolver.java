package org.truenewx.core.region.address;

import java.net.InetAddress;

import javax.annotation.Nullable;

/**
 * 网络地址->区划解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface InetAddressRegionResolver {
    /**
     * 获取指定网络地址对应的区划代号
     *
     * @param address
     *            网络地址
     * @return 区划代号
     */
    @Nullable
    String resolveRegionCode(InetAddress address);
}
