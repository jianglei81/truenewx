package org.truenewx.core.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 网络工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NetUtil {
    private NetUtil() {
    }

    /**
     * 获取指定主机名（域名）对应的IP地址
     *
     * @param host
     *            主机名（域名）
     * @return IP地址
     */
    public static String getIpByHost(final String host) {
        if (StringUtil.isIp(host)) {
            return host;
        }
        String s = "";
        try {
            final InetAddress address = InetAddress.getByName(host);
            for (final byte b : address.getAddress()) {
                s += (b & 0xff) + ".";
            }
            if (s.length() > 0) {
                s = s.substring(0, s.length() - 1);
            }
        } catch (final UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
        return s;
    }

    /**
     * 获取本机网卡IP地址
     *
     * @return 本机网卡IP地址
     */
    public static String getLocalIp() {
        try {
            final Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                final NetworkInterface ni = nis.nextElement();
                final Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    final String ip = ias.nextElement().getHostAddress();
                    if (NetUtil.isLanIp(ip) && !"127.0.0.1".equals(ip)) {
                        return ip;
                    }
                }
            }
        } catch (final SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    /**
     * 获取指定ip地址字符串转换成的IPv4网络地址对象。如果无法转换则返回null
     *
     * @param ip
     *            ip地址字符串
     * @return IPv4网络地址对象
     */
    public static Inet4Address getInet4Address(final String ip) {
        try {
            final InetAddress address = InetAddress.getByName(ip);
            if (address instanceof Inet4Address) {
                return (Inet4Address) address;
            }
        } catch (final UnknownHostException e) {
        }
        return null;
    }

    /**
     * 判断指定字符串是否局域网IP地址
     *
     * @param s
     *            字符串
     * @return true if 指定字符串是局域网IP地址, otherwise false
     */
    public static boolean isLanIp(final String s) {
        if (StringUtil.isIp(s)) {
            if (s.startsWith("192.168.") || s.startsWith("10.") || s.equals("127.0.0.1")
                    || s.equals("0:0:0:0:0:0:0:1")) {
                return true;
            } else if (s.startsWith("172.")) { // 172.16-172.31网段
                final String seg = s.substring(4, s.indexOf('.', 4)); // 取第二节
                final int value = MathUtil.parseInt(seg);
                if (16 <= value && value <= 31) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断指定网络地址是否局域网地址
     *
     * @param address
     *            网络地址
     * @return 指定网络地址是否局域网地址
     */
    public static boolean isLanAddress(final InetAddress address) {
        final byte[] b = address.getAddress();
        // 暂只考虑IPv4
        return b.length == 4
                && ((b[0] == 192 && b[1] == 168) || b[0] == 10
                        || (b[0] == 172 && b[1] >= 16 && b[1] <= 31) || (b[0] == 127 && b[1] == 0
                        && b[2] == 0 && b[3] == 1));
    }

    /**
     * 获取指定IP地址的整数表达形式
     *
     * @param address
     *            IP地址
     * @return 整数表达形式
     */
    public static int intValueOf(final InetAddress address) {
        // IPv4和IPv6的hashCode()即为其整数表达形式，本方法向调用者屏蔽该逻辑
        return address.hashCode();
    }
}
