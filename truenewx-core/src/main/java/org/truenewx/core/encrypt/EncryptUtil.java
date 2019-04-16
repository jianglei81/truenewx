package org.truenewx.core.encrypt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

/**
 * 加密工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class EncryptUtil {

    public static byte[] toBytes(Object source) {
        try {
            if (source instanceof File) {
                return FileUtils.readFileToByteArray((File) source);
            } else if (source instanceof InputStream) {
                return IOUtils.toByteArray((InputStream) source);
            } else if (source instanceof Reader) {
                return IOUtils.toByteArray((Reader) source);
            } else if (source instanceof byte[]) {
                return (byte[]) source;
            } else {
                return source.toString().getBytes();
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(Md5Encrypter.class).error(e.getMessage(), e);
            return null;
        }
    }

    public static String sha1(Object source) {
        try {
            byte[] data = toBytes(source);
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(data);
            byte digestBytes[] = digest.digest();
            StringBuffer s = new StringBuffer();
            for (int i = 0; i < digestBytes.length; i++) {
                String shaHex = Integer.toHexString(digestBytes[i] & 0xFF);
                if (shaHex.length() < 2) {
                    s.append(0);
                }
                s.append(shaHex);
            }
            return s.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
