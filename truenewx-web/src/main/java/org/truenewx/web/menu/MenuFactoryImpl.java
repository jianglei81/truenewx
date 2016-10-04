package org.truenewx.web.menu;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.menu.parse.MenuParser;

/**
 * 菜单工厂实现
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuFactoryImpl implements MenuFactory, InitializingBean {

    private MenuParser parser;
    private Resource[] resources;
    private Map<String, Menu> menus = new HashMap<String, Menu>();

    @Override
    public Menu getMenu(final String name) {
        return this.menus.get(name);
    }

    /**
     * @param parser
     *            parser
     */
    public void setParser(final MenuParser parser) {
        this.parser = parser;
    }

    /**
     * @param resources
     *            设置源集合
     */
    public void setResources(final Resource... resources) {
        this.resources = resources;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.resources != null) {
            for (final Resource resource : this.resources) {
                final Menu menu = this.parser.parser(resource.getInputStream());
                this.menus.put(menu.getName(), menu);
            }
        }
    }
}
