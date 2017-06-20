package org.truenewx.web.menu.view;

import org.truenewx.core.util.StringUtil;
import org.truenewx.core.util.TreeNode;
import org.truenewx.web.menu.model.MenuAction;
import org.truenewx.web.menu.model.MenuItem;
import org.truenewx.web.menu.model.MenuOperation;

/**
 * 菜单节点
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuNode extends TreeNode<String> {

    private static final long serialVersionUID = -5439357426707882473L;

    private String permission;

    public MenuNode(final MenuAction action) {
        super(StringUtil.uuid32(), action.getCaption());
        this.permission = action.getPermission();
        if (action instanceof MenuItem) {
            final MenuItem item = (MenuItem) action;
            for (final MenuItem subItem : item.getSubs()) {
                addSub(new MenuNode(subItem));
            }
            for (final MenuOperation operation : item.getOperations()) {
                addSub(new MenuNode(operation));
            }
        }
    }

    public String getPermission() {
        return this.permission;
    }

}
