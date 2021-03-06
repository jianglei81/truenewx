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

    private String base;
    private String build = "0";

    @Override
    public void afterInitialized(final ApplicationContext context) throws Exception {
        final String version = readFullVersion(context);
        if (version != null) {
            final int baseLevel = 3;
            final String[] fragments = version.split("\\.", baseLevel + 1);
            this.base = getVersion(fragments, baseLevel);
            if (fragments.length > baseLevel) {
                this.build = fragments[baseLevel];
            }
        }
    }

    protected String getVersion(final Object[] fragments, final int level) {
        final StringBuffer base = new StringBuffer();
        for (int i = 0; i < level; i++) {
            if (fragments.length > i) {
                base.append(fragments[i]);
            } else {
                base.append(0); // 不足的用0代替
            }
            base.append(Strings.DOT);
        }
        if (base.length() > 0) { // 去掉末尾的句点
            base.deleteCharAt(base.length() - 1);
        }
        return base.toString();
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
    protected abstract String readFullVersion(ApplicationContext context);

    @Override
    public String getBuild() {
        return this.build;
    }

    @Override
    public String getVersion(final boolean withBuild) {
        if (this.base == null) {
            return null;
        }
        if (withBuild) {
            return this.base + Strings.DOT + this.build;
        } else {
            return this.base;
        }
    }
}
