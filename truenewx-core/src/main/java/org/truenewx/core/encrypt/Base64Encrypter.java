package org.truenewx.core.encrypt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

/**
 * BASE64可逆算法加密器
 *
 * @author jianglei
 *
 */
public class Base64Encrypter implements Encrypter {
    public final static Base64Encrypter INSTANCE = new Base64Encrypter();

    private Base64Encrypter() {
    }

    @Override
    public String encrypt(final Object source) {
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
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            return null;
        }
        return Base64.encodeBase64String(data).replaceAll("\n", "");
    }

    public String decrypt(final String encryptedText) {
        final byte[] bytes = Base64.decodeBase64(encryptedText);
        return bytes == null ? null : new String(bytes);
    }
}
