package org.truenewx.core.model;

/**
 * 用户标识，用于唯一确定整个系统中的一个用户
 *
 * @author jianglei
 *
 */
public interface UserIdentity {

    @Override
    /**
     * 必须实现，以用字符串形式表达唯一用户
     */
    String toString();

}
