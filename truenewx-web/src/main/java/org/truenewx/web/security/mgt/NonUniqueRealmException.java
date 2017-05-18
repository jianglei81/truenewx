package org.truenewx.web.security.mgt;

/**
 * 非唯一Realm异常
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NonUniqueRealmException extends RuntimeException {

    private static final long serialVersionUID = 8433972560900325977L;

    public NonUniqueRealmException() {
        super("There are more than one Realms");
    }

}
