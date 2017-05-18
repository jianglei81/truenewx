package org.truenewx.web.security.login;

/**
 * 用户名+密码形式的登录Token，类似Shiro中的UsernamePasswordToken
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UsernamePasswordToken implements RememberMeLoginToken, HostLoginToken {

    private String username;

    private String password;

    private boolean rememberMe;

    private String host;

    public UsernamePasswordToken() {
    }

    public UsernamePasswordToken(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    public void setRememberMe(final boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public boolean isRememberMe() {
        return this.rememberMe;
    }

    @Override
    public Object getPrincipal() {
        return getUsername();
    }

    @Override
    public Object getCredentials() {
        return getPassword();
    }

}
