package org.truenewx.core.spring.core.io;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

/**
 * 包资源扫描器，扫描指定包及其子包，查找满足指定文件名样式的文件资源
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PackageResourceScaner {
    public static final PackageResourceScaner INSTANCE = new PackageResourceScaner();
    private static final String PATH_PATTERN = "/**/";

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private PackageResourceScaner() {
    }

    public List<Resource> scan(final String[] packages, final String[] fileNamePatterns) {
        final List<Resource> list = new ArrayList<Resource>();
        if (packages != null && fileNamePatterns != null) {
            for (final String pkg : packages) {
                for (final String fileNamePattern : fileNamePatterns) {
                    try {
                        final String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                                + ClassUtils.convertClassNameToResourcePath(pkg) + PATH_PATTERN
                                + fileNamePattern;
                        final Resource[] resources = this.resourcePatternResolver
                                .getResources(pattern);
                        for (final Resource resource : resources) {
                            list.add(resource);
                        }
                    } catch (final Throwable e) {
                        this.logger.warn(e.getMessage());
                    }
                }
            }
        }
        return list;
    }

}
