package org.truenewx.web.login;

/**
 * 自动登录器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AutoLoginer extends Loginer {

    /**
     *
     * @return 保存登录名的Cookie名称
     */
    String getLoginNameCookieName();

    /**
     *
     * @return 保存密码的Cookie名称
     */
    String getPasswordCookieName();

}
