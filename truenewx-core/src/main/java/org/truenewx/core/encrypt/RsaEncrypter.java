package org.truenewx.core.encrypt;

import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.security.Key;

import javax.crypto.Cipher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

/**
 * RSA加密器
 *
 * @author jianglei
 *
 */
public final class RsaEncrypter {

    /**
     * 加密
     *
     * @param source
     *            源数据
     * @param publicKey
     *            公钥
     * @return
     * @throws Exception
     *
     * @author jianglei
     */
    public static String encrypt(final byte[] source, final InputStream publicKey)
            throws Exception {
        /** 将文件中的公钥对象读出 */
        final ObjectInputStream ois = new ObjectInputStream(publicKey);
        final Key key = (Key) ois.readObject();
        ois.close();
        /** 得到Cipher对象来实现对源数据的RSA加密 */
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        /** 执行加密操作 */
        final byte[] b1 = cipher.doFinal(source);
        return Base64Encrypter.INSTANCE.encrypt(b1);
    }

    /**
     * 解密
     *
     * @param cryptograph
     *            密文
     * @param privateKey
     *            私钥
     * @return
     * @throws Exception
     *
     * @author jianglei
     */
    public static String decrypt(final String cryptograph, final InputStream privateKey)
            throws Exception {
        /** 将文件中的私钥对象读出 */
        final ObjectInputStream ois = new ObjectInputStream(privateKey);
        final Key key = (Key) ois.readObject();
        ois.close();
        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return Base64Encrypter.INSTANCE.decrypt(cryptograph);
    }

    /**
     * 加密
     *
     * @param source
     *            源数据
     * @param publicKey
     *            公钥
     * @return
     * @throws Exception
     *
     * @author jianglei
     */
    public static String encrypt(final Object source, final InputStream publicKey) {
        byte[] data;
        try {
            if (source instanceof File) {
                data = FileUtils.readFileToByteArray((File) source);
            } else if (source instanceof InputStream) {
                data = IOUtils.toByteArray((InputStream) source);
            } else if (source instanceof Reader) {
                data = IOUtils.toByteArray((Reader) source);
            } else if (source instanceof byte[]) {
                data = (byte[]) source;
            } else {
                data = source.toString().getBytes();
            }
            encrypt(data, publicKey);
        } catch (final Exception e) {
            LoggerFactory.getLogger(RsaEncrypter.class).error(e.getMessage(), e);
            return null;
        }
        return null;
    }

}
