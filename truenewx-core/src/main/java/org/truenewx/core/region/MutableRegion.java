package org.truenewx.core.region;

import java.util.Collection;

/**
 * 内容可变的行政区划
 *
 * @author jianglei
 *
 */
public class MutableRegion extends Region {

    public MutableRegion() {
        super(null, null);
    }

    @Override
    public void setCode(final String code) {
        super.setCode(code);
    }

    @Override
    public void setCaption(final String caption) {
        super.setCaption(caption);
    }

    @Override
    public void setGroup(final String group) {
        super.setGroup(group);
    }

    public void setSubCollection(final Collection<Region> subCollection) {
        for (final Region sub : subCollection) {
            addSub(sub);
        }
    }

}
