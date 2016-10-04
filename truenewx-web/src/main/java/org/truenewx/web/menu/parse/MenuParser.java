package org.truenewx.web.menu.parse;

import java.io.InputStream;

import org.truenewx.web.menu.model.Menu;

/**
 * 菜单解析器
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public interface MenuParser {

    /**
     * 解析菜单XML
     * 
     * @param inputStream
     *            菜单流
     * @return 菜单
     */
    public Menu parser(InputStream inputStream);
}
