package org.truenewx.core.spring.core.io;

import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.truenewx.core.Strings;
import org.truenewx.core.util.StringUtil;

/**
 * 忽略文件名的文件过滤器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class IgnoreFilenameFilter implements IOFileFilter {
    private String[] ignoredPatterns;

    public IgnoreFilenameFilter(final String ignoredPattern) {
        setIgnoredPattern(ignoredPattern);
    }

    public void setIgnoredPattern(final String ignoredPattern) {
        this.ignoredPatterns = ignoredPattern.split(",");
    }

    @Override
    public boolean accept(final File dir, final String name) {
        final String path = dir.getAbsolutePath() + Strings.SLASH + name;
        return !StringUtil.wildcardMatchOneOf(path, this.ignoredPatterns);
    }

    @Override
    public boolean accept(final File dir) {
        final String path = dir.getAbsolutePath();
        return !StringUtil.wildcardMatchOneOf(path, this.ignoredPatterns);
    }

}
