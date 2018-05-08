package org.truenewx.data.user;

/**
 * 可获取用户标识的
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface UserIdentifiable {

    /**
     *
     * @return 用户标识
     */
    UserIdentity getUserIdentity();

}
