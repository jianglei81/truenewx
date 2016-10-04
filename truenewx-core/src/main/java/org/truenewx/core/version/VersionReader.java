package org.truenewx.core.version;

/**
 * 版本号读取器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface VersionReader {

    /**
     *
     * @return 内部构建版本号
     */
    String getBuildVersion();

    /**
     *
     * @return 对外发布版本号
     */
    String getReleaseVersion();

}
