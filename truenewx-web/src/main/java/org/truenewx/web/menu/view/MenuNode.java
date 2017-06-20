package org.truenewx.web.menu.view;

import org.truenewx.core.util.StringUtil;
import org.truenewx.core.util.TreeNode;
import org.truenewx.web.menu.model.MenuItem;

/**
 * 菜单节点
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuNode extends TreeNode<String> {

    private static final long serialVersionUID = -5439357426707882473L;

    private String permission;
    private boolean selected;
    private boolean expanded;

    public MenuNode(final MenuItem item) {
        super(StringUtil.uuid32(), item.getCaption());
        this.permission = item.getPermission();
        for (final MenuItem subItem : item.getSubs()) {
            addSub(new MenuNode(subItem));
        }
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }

}
