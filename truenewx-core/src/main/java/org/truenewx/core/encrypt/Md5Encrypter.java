package org.truenewx.core.encrypt;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密器
 *
 * @author jianglei
 *
 */
public class Md5Encrypter implements Encrypter {

    public static final Md5Encrypter INSTANCE = new Md5Encrypter();

    public static String encrypt32(Object source) {
        byte[] data = EncryptUtil.toBytes(source);
        return DigestUtils.md5Hex(data);
    }

    public static String encrypt16(Object source) {
        String s = encrypt32(source);
        return s.substring(8, 24);
    }

    private Md5Encrypter() {
    }

    @Override
    public String encrypt(Object source) {
        return encrypt32(source);
    }

}
