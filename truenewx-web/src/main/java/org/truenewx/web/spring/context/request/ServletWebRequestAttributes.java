package org.truenewx.web.spring.context.request;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.truenewx.core.util.BeanUtil;

/**
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ServletWebRequestAttributes extends ServletWebRequest {

    private ServletRequestAttributes attributes;

    public ServletWebRequestAttributes(final ServletRequestAttributes attributes,
            final HttpServletResponse response) {
        super(attributes.getRequest(), response);
        this.attributes = attributes;
    }

    @Override
    public void requestCompleted() {
        if (isRequestActive()) {
            this.attributes.requestCompleted();
            BeanUtil.setFieldValue(this, "requestActive", false);
        }
    }

}
