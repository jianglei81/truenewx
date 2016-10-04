package org.truenewx.web.spring.servlet;

/**
 * 在ServletContext初始后加载并作为ServletContext属性保存的bean
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public interface ServletContextInitBean {
    /**
     * 获取保存到ServletContext时使用的名称
     * 
     * @return 保存到ServletContext时使用的名称
     */
    public String getAttributeName();
}
