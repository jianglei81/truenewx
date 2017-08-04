package org.truenewx.web.version;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import org.truenewx.core.version.VersionReader;
import org.truenewx.web.tagext.DynamicAttributeTagSupport;

/**
 * 版本号标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class VersionTag extends DynamicAttributeTagSupport {

    private static final long serialVersionUID = 5695596617003790014L;

    private static final String TYPE_BUILD = "build";
    private static final String TYPE_RELEASE = "release";
    private static final String PROJECT_BUILD = "project.build";

    private String type = TYPE_RELEASE;
    private String prefix;
    private boolean cacheBuild = true;

    public void setType(final String type) {
        this.type = type;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public void setCacheBuild(final boolean cacheBuild) {
        this.cacheBuild = cacheBuild;
    }

    @Override
    public void doTag() throws JspException, IOException {
        final VersionReader versionReader = getBeanFromApplicationContext(VersionReader.class);
        if (versionReader != null) {
            String version;
            switch (this.type) {
            case TYPE_BUILD:
                version = versionReader.getBuildVersion();
                if (version.endsWith(".0")) { // 构建版本以.0结尾，说明没有配置构建号
                    version = version.substring(0, version.length() - 1) + getBuild();
                }
                break;
            case TYPE_RELEASE:
                version = versionReader.getReleaseVersion();
                break;
            default:
                version = null;
            }
            if (version != null) {
                if (this.prefix != null) {
                    version = this.prefix + version;
                }
                print(version);
            }
        }
    }

    private String getBuild() {
        if (this.cacheBuild) {
            final ServletContext servletContext = this.pageContext.getServletContext();
            String build = (String) servletContext.getAttribute(PROJECT_BUILD);
            if (build == null) {
                build = String.valueOf(System.currentTimeMillis());
                servletContext.setAttribute(PROJECT_BUILD, build);
            }
            return build;
        } else {
            return String.valueOf(System.currentTimeMillis());
        }
    }

}
