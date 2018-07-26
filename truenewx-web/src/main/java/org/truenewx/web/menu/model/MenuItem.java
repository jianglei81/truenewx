package org.truenewx.web.menu.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.core.spring.core.env.functor.FuncProfile;

/**
 * 菜单项类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MenuItem extends AbstractMenuItem implements Serializable {

    private static final long serialVersionUID = -6145127565332857618L;

    public MenuItem(String type) {
        super(type);
    }

    public boolean isProfileFitted() {
        final String profile = FuncProfile.INSTANCE.apply();
        return StringUtils.isBlank(profile) || getProfiles().isEmpty()
                || getProfiles().contains(profile);
    }

    @Override
    public MenuItem clone() {
        MenuItem item = new MenuItem(getType());
        item.getProfiles().addAll(getProfiles());
        item.getOptions().putAll(getOptions());
        return item;
    }

}
