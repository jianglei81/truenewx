package org.truenewx.core.model;

/**
 * 命名支持
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class NamedSupport implements Named {
    private String name;

    protected NamedSupport(final String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    protected void setName(final String name) {
        this.name = name;
    }

}
