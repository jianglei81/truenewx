package org.truenewx.web.region.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.context.ApplicationContext;
import org.truenewx.core.region.RegionOption;
import org.truenewx.core.region.RegionOptionSource;
import org.truenewx.core.spring.util.SpringUtil;
import org.truenewx.web.spring.util.SpringWebUtil;

/**
 * 区划显示标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class RegionViewTag extends TagSupport {

    private static final long serialVersionUID = 7264526360901106055L;

    /**
     * 行政区划代号
     */
    private String value;

    /**
     * 分隔符
     */
    private String delimiter = "";

    /**
     * 起始层级
     */
    private int startLevel = 2;

    /**
     * 结束层级
     */
    private int endLevel = 4;

    /**
     * @param delimiter
     *            分隔符
     */
    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @param value
     *            行政区划代号
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @param startLevel
     *            起始层级
     */
    public void setStartLevel(final int startLevel) {
        this.startLevel = startLevel;
    }

    /**
     * @param endLevel
     *            结束层级
     */
    public void setEndLevel(final int endLevel) {
        this.endLevel = endLevel;
    }

    public String getParentCaptions() {
        final ApplicationContext context = SpringWebUtil.getApplicationContext(this.pageContext);
        final RegionOptionSource regionOptionSource = SpringUtil.getFirstBeanByClass(context,
                RegionOptionSource.class);
        final RegionOption option = regionOptionSource.getRegionOption(this.value,
                this.pageContext.getRequest().getLocale());

        final List<String> options = new ArrayList<>();
        final StringBuffer caption = new StringBuffer();
        if (option != null) {
            RegionOption parent = option.getParent();
            options.add(option.getCaption());
            while (parent != null) {
                options.add(0, parent.getCaption());
                parent = parent.getParent();
            }
            for (int i = this.startLevel - 1; i < options.size(); i++) {
                caption.append(options.get(i)).append(this.delimiter);
                if (i >= this.endLevel - 1) {
                    break;
                }
            }
            if (caption.length() > 0) {
                caption.delete(caption.length() - this.delimiter.length(), caption.length());
            }
        }
        return caption.toString();
    }

    @Override
    public int doEndTag() throws JspException {
        final JspWriter out = this.pageContext.getOut();
        try {
            out.print(this.getParentCaptions());
        } catch (final IOException e) {
            throw new JspException(e);
        }
        return Tag.EVAL_PAGE;
    }
}
