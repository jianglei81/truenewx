package org.truenewx.web.menu;

import java.util.List;
import java.util.Map;

import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.security.authority.Authority;
import org.truenewx.web.security.authority.Authorization;

/**
 * 菜单解决器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface MenuResolver {

    /**
     * 获取完整菜单
     *
     * @return 完整菜单
     */
    public Menu getFullMenu();

    /**
     * 获取仅包含指定授权集动作的菜单
     *
     * @param authorization
     *            授权集
     *
     * @return 仅包含指定授权集动作的菜单
     */
    public Menu getAuthorizedMenu(Authorization authorization);

    /**
     * 获取指定选项映射集限定的授权清单
     *
     * @return 指定选项映射集限定的授权清单
     */
    public List<Authority> getAuthorites(Map<String, Object> options);

}
