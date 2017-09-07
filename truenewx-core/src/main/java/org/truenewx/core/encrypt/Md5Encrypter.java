package org.truenewx.core.encrypt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

/**
 * MD5加密器
 *
 * @author jianglei
 *
 */
public final class Md5Encrypter implements Encrypter {

    public static String encrypt32(final Object source) {
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
        } catch (final IOException e) {
            LoggerFactory.getLogger(Md5Encrypter.class).error(e.getMessage(), e);
            return null;
        }
        return DigestUtils.md5Hex(data);
    }

    public static String encrypt16(final Object source) {
        final String s = encrypt32(source);
        return s.substring(8, 24);
    }

    @Override
    public String encrypt(final Object source) {
        return encrypt32(source);
    }

}
