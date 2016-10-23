package org.truenewx.core.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.truenewx.core.Strings;

import freemarker.cache.TemplateLoader;

/**
 * 将模板文件路径作为模板内容加载的模板加载器
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class NameAsContentTemplateLoader implements TemplateLoader {

    @Override
    public Object findTemplateSource(String name) throws IOException {
        final int index1 = name.lastIndexOf(Strings.UNDERLINE);
        if (index1 > 0 && index1 == name.length() - 3) {
            final int index0 = name.substring(0, index1).lastIndexOf(Strings.UNDERLINE);
            if (index0 > 0 && index0 == index1 - 3) {
                name = name.substring(0, index0);
            }
        }
        return new StringReader(name);
    }

    @Override
    public long getLastModified(final Object templateSource) {
        return 0;
    }

    @Override
    public Reader getReader(final Object templateSource, final String encoding) throws IOException {
        return (Reader) templateSource;
    }

    @Override
    public void closeTemplateSource(final Object templateSource) throws IOException {
        ((Reader) templateSource).close();
    }

}
