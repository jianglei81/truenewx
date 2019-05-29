package org.truenewx.data.user;

/**
 * 整数型用户标识
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class IntegerUserIdentity implements UserIdentity {

    private static final long serialVersionUID = -1388580489232151252L;

    private int value;

    public IntegerUserIdentity(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
