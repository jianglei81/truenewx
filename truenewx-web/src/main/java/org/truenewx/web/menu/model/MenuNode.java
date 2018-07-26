package org.truenewx.web.menu.model;

import org.truenewx.core.util.StringUtil;
import org.truenewx.core.util.TreeNode;
import org.truenewx.web.security.authority.Authority;

/**
 * 菜单节点
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuNode extends TreeNode<String> {

    private static final long serialVersionUID = -5439357426707882473L;

    private String permission;

    public MenuNode(final ActableMenuItem item) {
        super(StringUtil.uuid32(), item.getCaption());
        final Authority authority = item.getAuthority();
        if (authority != null) {
            this.permission = authority.getPermission();
        }
        item.getSubs().forEach(sub -> {
            if (sub instanceof ActableMenuItem) {
                addSub(new MenuNode((ActableMenuItem) sub));
            }
        });
    }

    public String getPermission() {
        return this.permission;
    }

}
