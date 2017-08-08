package org.truenewx.core.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.truenewx.core.spring.util.PlaceholderResolver;

/**
 * 基于占位符的版本号读取器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PlaceholderVersionReader extends AbstractVersionReader {

    private PlaceholderResolver placeholderResolver;
    private String code = "project.version";

    @Autowired(required = false)
    public void setPlaceholderResolver(final PlaceholderResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    protected String readFullVersion(final ApplicationContext context) {
        if (this.placeholderResolver != null) {
            return this.placeholderResolver.resolvePlaceholder(this.code);
        }
        return null;
    }

}
