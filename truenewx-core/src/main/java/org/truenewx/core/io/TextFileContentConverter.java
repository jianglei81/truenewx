package org.truenewx.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * 文本文件内容转换器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TextFileContentConverter implements ApplicationContextAware, FileContentConverter {

    private TextContentConverter textContentConverter;
    private ApplicationContext context;

    public void setTextContentConverter(final TextContentConverter textContentConverter) {
        this.textContentConverter = textContentConverter;
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void convert(final String locationPattern, final String encoding) {
        try {
            final Resource[] resources = this.context.getResources(locationPattern);
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
