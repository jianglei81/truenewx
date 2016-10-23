package org.truenewx.core.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.truenewx.core.parser.util.FreeMarkerUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 通过FreeMarker实现的模板解析器
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class FreeMarkerTemplateParser implements TemplateParser {

    private Configuration config = FreeMarkerUtil.getDefaultConfiguration();

    /**
     * @see freemarker.core.Configurable#setNumberFormat(java.lang.String)
     */
    public void setNumberFormat(final String numberFormat) {
        this.config.setNumberFormat(numberFormat);
    }

    /**
     * @see freemarker.core.Configurable#setTimeFormat(java.lang.String)
     */
    public void setTimeFormat(final String timeFormat) {
        this.config.setTimeFormat(timeFormat);
    }

    /**
     * @see freemarker.core.Configurable#setDateFormat(java.lang.String)
     */
    public void setDateFormat(final String dateFormat) {
        this.config.setDateFormat(dateFormat);
    }

    /**
     * @see freemarker.core.Configurable#setDateTimeFormat(java.lang.String)
     */
    public void setDateTimeFormat(final String dateTimeFormat) {
        this.config.setDateTimeFormat(dateTimeFormat);
    }

    /**
     * @see freemarker.template.Configuration#setDefaultEncoding(java.lang.String)
     */
    public void setDefaultEncoding(final String encoding) {
        this.config.setDefaultEncoding(encoding);
    }

    @Override
    public String parse(final String templateContent, final Map<String, ?> params,
            final Locale locale) {
        final Configuration config = (Configuration) this.config.clone();
        if (locale != null) {
            config.setLocale(locale);
        }
        final StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("template", templateContent);
        config.setTemplateLoader(stringLoader);
        try {
            final Template t = config.getTemplate("template");
            final StringWriter out = new StringWriter();
            t.process(params, out);
            return out.toString();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final TemplateException e) {
            e.printStackTrace();
            // 模板格式错误，返回模板本身
            return templateContent;
        }
        return null;
    }

    @Override
    public String parse(final File templateFile, final Map<String, ?> params, final Locale locale)
            throws IOException {
        final Configuration config = (Configuration) this.config.clone();
        if (locale != null) {
            config.setLocale(locale);
        }
        try {
            config.setDirectoryForTemplateLoading(templateFile.getParentFile());
            final Template t = config.getTemplate(templateFile.getName());
            final StringWriter out = new StringWriter();
            t.process(params, out);
            return out.toString();
        } catch (final TemplateException e) {
            e.printStackTrace();
            // 模板格式错误，返回模板内容
            return IOUtils.toString(new FileInputStream(templateFile));
        }
    }
}
