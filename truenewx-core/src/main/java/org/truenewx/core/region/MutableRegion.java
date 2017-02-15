package org.truenewx.core.region;

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

    public void setSubs(final MutableRegion[] subs) {
        for (final Region sub : subs) {
            addSub(sub);
        }
    }

}
