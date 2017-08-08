package org.truenewx.web.version;

import java.io.IOException;

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

    private static final String BUILD_ONLY = "only";

    private String build;
    private String prefix;

    public void setBuild(final String build) {
        this.build = build;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void doTag() throws JspException, IOException {
        final VersionReader versionReader = getBeanFromApplicationContext(VersionReader.class);
        if (versionReader != null) {
            String version;
            if (BUILD_ONLY.equals(this.build)) {
                version = versionReader.getBuild();
            } else {
                version = versionReader.getVersion(Boolean.valueOf(this.build));
            }
            if (version != null) {
                if (this.prefix != null) {
                    version = this.prefix + version;
                }
                print(version);
            }
        }
    }

}
