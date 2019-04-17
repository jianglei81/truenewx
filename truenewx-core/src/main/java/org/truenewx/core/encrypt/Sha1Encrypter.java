package org.truenewx.core.encrypt;

import org.truenewx.core.util.EncryptUtil;

/**
 * SHA1加密器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class Sha1Encrypter implements Encrypter {

    @Override
    public String encrypt(Object source) {
        return EncryptUtil.encryptBySha1(source);
    }

}
