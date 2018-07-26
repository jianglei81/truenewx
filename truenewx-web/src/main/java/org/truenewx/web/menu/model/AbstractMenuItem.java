package org.truenewx.web.menu.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 抽象的菜单项
 *
 * @author jianglei
 * @since JDK 1.8
 */
abstract class AbstractMenuItem {

    /**
     * 类型
     */
    private String type;
    /**
     * 可见环境集合
     */
    private Set<String> profiles = new HashSet<>();
    /**
     * 配置映射集
     */
    private Map<String, Object> options = new HashMap<>();

    public AbstractMenuItem(String type) {
        this.type = type;
    }

    /**
     *
     * @return 类型
     */
    public String getType() {
        return this.type;
    }

    /**
     *
     * @return 可见环境集合
     */
    public Set<String> getProfiles() {
        return this.profiles;
    }

    /**
     * @return 配置映射集
     */
    public Map<String, Object> getOptions() {
        return this.options;
    }

}
