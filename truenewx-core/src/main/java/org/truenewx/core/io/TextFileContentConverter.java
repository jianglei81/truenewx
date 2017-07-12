package org.truenewx.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 文本文件内容转换器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TextFileContentConverter implements FileContentConverter {

    private TextContentConverter textContentConverter;
    private ResourcePatternResolver resourcePatternResolver;

    public void setTextContentConverter(final TextContentConverter textContentConverter) {
        this.textContentConverter = textContentConverter;
    }

    @Autowired
    public void setResourcePatternResolver(final ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    public void convert(final String locationPattern, final String encoding) {
        try {
            final Resource[] resources = this.resourcePatternResolver.getResources(locationPattern);
            if (resources.length == 0) {
                throw new FileNotFoundException(locationPattern);
            }
            for (final Resource resource : resources) {
                final File file = resource.getFile();
                final FileInputStream in = new FileInputStream(file);
                String content = IOUtils.toString(in, encoding);
                in.close();

                content = this.textContentConverter.convert(content);

                final FileOutputStream out = new FileOutputStream(file);
                IOUtils.write(content, out);
                out.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
