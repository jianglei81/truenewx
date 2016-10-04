package org.truenewx.core.spring.core.io;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * 基于classpath的上下文资源
 *
 * @author jianglei
 * @since JDK 1.8
 */
class ClassPathContextResource extends ClassPathResource implements ContextResource {

    public ClassPathContextResource(final String path, final ClassLoader classLoader) {
        super(path, classLoader);
    }

    @Override
    public String getPathWithinContext() {
        return getPath();
    }

    @Override
    public Resource createRelative(final String relativePath) {
        final String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
        return new ClassPathContextResource(pathToUse, getClassLoader());
    }
}
