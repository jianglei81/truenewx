package org.truenewx.core.version;

import org.springframework.context.ApplicationContext;
import org.truenewx.core.Strings;
import org.truenewx.core.spring.beans.ContextInitializedBean;

/**
 * 抽象的版本号读取器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractVersionReader implements VersionReader, ContextInitializedBean {

    private int[] versionArray; // 版本号数组缓存

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        this.versionArray = readVersionArray(context);
    }

    protected String getVersionNumber(final int[] versionArray, final int number) {
        if (versionArray != null) {
            final StringBuffer version = new StringBuffer();
            for (int i = 0; i < number; i++) {
                if (versionArray.length > i) {
                    version.append(versionArray[i]);
                } else {
                    version.append(0); // 不足的用0代替
                }
                version.append(Strings.DOT);
            }
            if (version.length() > 0) { // 去掉末尾的句点
                version.deleteCharAt(version.length() - 1);
            }
            return version.toString();
        }
        return null;
    }

    /**
     * 读取版本号数组
     *
     * @param context
     *            Spring容器上下文
     *
     * @return 版本号数组
     *
     * @author jianglei
     */
    protected abstract int[] readVersionArray(ApplicationContext context);

    @Override
    public String getBuildVersion() {
        return getVersionNumber(this.versionArray, 4);
    }

    @Override
    public String getReleaseVersion() {
        return getVersionNumber(this.versionArray, 3);
    }
}
