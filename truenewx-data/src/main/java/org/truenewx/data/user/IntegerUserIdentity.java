package org.truenewx.data.user;

/**
 * 整数型用户标识
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class IntegerUserIdentity implements UserIdentity {

    private static final long serialVersionUID = -1388580489232151252L;

    private Integer value;

    public IntegerUserIdentity(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

}
