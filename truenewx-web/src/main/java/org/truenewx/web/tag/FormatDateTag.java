package org.truenewx.web.tag;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.truenewx.core.util.DateUtil;
import org.truenewx.core.util.TemporalUtil;

/**
 * 格式化日期输出标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class FormatDateTag extends SimpleTagSupport {

    private Object value;
    private String pattern;

    public void setValue(Object value) {
        this.value = value;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void doTag() throws JspException, IOException {
        String result = null;
        if (this.value instanceof Date) {
            if (this.pattern == null) {
                this.pattern = DateUtil.LONG_DATE_PATTERN;
            }
            result = DateUtil.format((Date) this.value, this.pattern);
        } else if (this.value instanceof Instant) {
            if (this.pattern == null) {
                this.pattern = DateUtil.LONG_DATE_PATTERN;
            }
            result = TemporalUtil.format((Instant) this.value, this.pattern);
        } else if (this.value instanceof LocalDate) {
            if (this.pattern == null) {
                this.pattern = DateUtil.SHORT_DATE_PATTERN;
            }
            result = TemporalUtil.format((LocalDate) this.value, this.pattern);
        } else if (this.value instanceof LocalTime) {
            if (this.pattern == null) {
                this.pattern = DateUtil.TIME_PATTERN;
            }
            result = TemporalUtil.format((LocalTime) this.value, this.pattern);
        } else if (this.value instanceof LocalDateTime) {
            if (this.pattern == null) {
                this.pattern = DateUtil.LONG_DATE_PATTERN;
            }
            result = TemporalUtil.format((LocalDateTime) this.value, this.pattern);
        }
        if (result != null) {
            JspWriter out = getJspContext().getOut();
            out.print(result);
        }
    }

}
