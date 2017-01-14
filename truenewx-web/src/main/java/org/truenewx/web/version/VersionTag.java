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

    private static final String TYPE_BUILD = "build";
    private static final String TYPE_RELEASE = "release";

    private String type = TYPE_RELEASE;
    private String prefix;

    public void setType(final String type) {
        this.type = type;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void doTag() throws JspException, IOException {
        final VersionReader versionReader = getBeanFromApplicationContext(VersionReader.class);
        if (versionReader != null) {
            String version;
            switch (this.type) {
            case TYPE_BUILD:
                version = versionReader.getBuildVersion();
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

}
