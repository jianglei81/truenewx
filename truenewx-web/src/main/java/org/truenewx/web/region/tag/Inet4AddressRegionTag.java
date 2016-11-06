package org.truenewx.web.region.tag;

import java.net.Inet4Address;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.truenewx.core.util.NetUtil;

/**
 * IPv4-区划映射标签
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class Inet4AddressRegionTag extends TagSupport {
    private static final long serialVersionUID = -2167890700680767525L;

    private String ip;

    public void setIp(final String ip) throws JspException {
        this.ip = (String) ExpressionEvaluatorManager.evaluate("ip", ip, String.class,
                this.pageContext);
    }

    @Override
    public int doEndTag() throws JspException {
        if (this.ip == null) {
            final ServletRequest request = this.pageContext.getRequest();
            this.ip = request.getRemoteAddr();
        }
        final Inet4Address address = NetUtil.getInet4Address(this.ip);
        if (address != null) {

        }
        return EVAL_PAGE;
    }
}
