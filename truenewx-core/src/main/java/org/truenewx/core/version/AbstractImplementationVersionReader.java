package org.truenewx.core.version;

import org.truenewx.core.Strings;

/**
 * 从MANIFEST.MF文件中读取实现版本号的抽象版本号读取器<br/>
 * 注意：使用时应创建位于目标jar包中的子类，以便读取MANIFEST.MF文件，且确保其中包含Implementation-Version属性
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractImplementationVersionReader implements VersionReader {

    @Override
    public String getBuildVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public String getReleaseVersion() {
        final String buildVersion = getBuildVersion();
        if (buildVersion == null) {
            return null;
        }
        final StringBuffer releaseVersion = new StringBuffer();
        final String[] versions = buildVersion.split("\\.");
        for (int i = 0; i < versions.length; i++) {
            if (i > 2) { // 最多只考虑前3段
                break;
            }
            releaseVersion.append(versions[i]).append(Strings.DOT);
        }
        if (releaseVersion.length() > 0) {
            releaseVersion.deleteCharAt(releaseVersion.length() - 1);
        }
        return releaseVersion.toString();
    }

}
