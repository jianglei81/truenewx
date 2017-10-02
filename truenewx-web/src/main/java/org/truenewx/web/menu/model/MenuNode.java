package org.truenewx.web.menu.model;

import org.truenewx.core.util.StringUtil;
import org.truenewx.core.util.TreeNode;

/**
 * 菜单节点
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuNode extends TreeNode<String> {

    private static final long serialVersionUID = -5439357426707882473L;

    private String permission;

    public MenuNode(final MenuItem item) {
        super(StringUtil.uuid32(), item.getCaption());
        this.permission = item.getPermission();
        item.getSubs().forEach(sub -> addSub(new MenuNode(sub)));
    }

    public String getPermission() {
        return this.permission;
    }

}
