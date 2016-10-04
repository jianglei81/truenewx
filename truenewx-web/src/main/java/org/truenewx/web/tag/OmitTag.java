package org.truenewx.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.web.tagext.SimpleDynamicAttributeTagSupport;

/**
 * 截取字符串标签
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class OmitTag extends SimpleDynamicAttributeTagSupport {

    private final static String REPLACE_OPERATOR = "...";

    /**
     * 显示长度
     */
    private int length;

    /**
     * 需处理超长的文本
     */
    private String value;

    /**
     * @param length
     *            显示长度
     * 
     * @author jianglei
     */
    public void setLength(final int length) {
        this.length = length;
    }

    /**
     * @param value
     *            需处理超长的文本
     * 
     * @author jianglei
     */
    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (StringUtils.isNotEmpty(this.value) && this.length > 0) {
            if (this.value.length() <= this.length) {
                print(this.value);
            } else {
                print(this.value.substring(0, this.length - 1) + REPLACE_OPERATOR);
            }
        }
    }

}
