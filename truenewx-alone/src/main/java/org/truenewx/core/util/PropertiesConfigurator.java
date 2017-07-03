package org.truenewx.core.util;

import java.util.Map;

/**
 * 属性集配置器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PropertiesConfigurator implements PropertiesProvider {

    private Map<String, String> properties;

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

}
