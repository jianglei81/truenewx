package org.truenewx.data.user;

/**
 * 用户名密码
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class UsernamePassword {

    private String username;
    private String password;

    public UsernamePassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

}
