package org.truenewx.core.encrypt;

import org.truenewx.core.util.EncryptUtil;

/**
 * MD5加密器
 *
 * @author jianglei
 *
 */
public class Md5Encrypter implements Encrypter {

    @Override
    public String encrypt(Object source) {
        return EncryptUtil.encryptByMd5(source);
    }

}
