package org.truenewx.core.version;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.truenewx.core.util.MathUtil;

/**
 * 基于属性文件的版本号读取器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PropertiesVersionReader extends AbstractVersionReader {

    private String path;
    private String code = "version";

    public PropertiesVersionReader(final String path) {
        Assert.notNull(path);
        this.path = path;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    protected int[] readVersionArray(final ApplicationContext context) {
        final Resource location = context.getResource(this.path);
        if (location.exists()) {
            try {
                final Properties properties = new Properties();
                properties.load(location.getInputStream());
                final String version = properties.getProperty(this.code);
                return MathUtil.parseIntArray(version, "\\.");
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
