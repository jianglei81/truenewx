package org.truenewx.core.encrypt;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展的MD5加密器
 *
 * @author jianglei
 *
 */
public class Md5xEncrypter implements KeyEncrypter {
    /**
     * 密文长度
     */
    public static final int ENCRYPTED_TEXT_LENGTH = 64;

    /**
     * MD5加密长度
     */
    public static final int MD5_ENCRYPT_LENGTH = 32;

    private final long staticKey;

    public Md5xEncrypter(final long staticKey) {
        this.staticKey = staticKey;
    }

    public String encryptByMd5Source(final String md5Source, final Object secretKey) {
        return encryptByMd5Source(md5Source, secretKey, this.staticKey);
    }

    @Override
    public String encrypt(final Object source, final Object secretKey) {
        final String md5Source = Md5Encrypter.encrypt32(source);
        return encryptByMd5Source(md5Source, secretKey);
    }

    public boolean validate(final String encryptedText, final Object source,
            final Object secretKey) {
        final String md5Source = Md5Encrypter.encrypt32(source);
        return validateByMd5Source(encryptedText, md5Source, secretKey);
    }

    private Integer[] getMd5SourceCharIndexes(final long staticKey, final int maxIndex) {
        final char[] c = Md5Encrypter.encrypt32(staticKey).toCharArray();
        final int length = c.length;
        final List<Integer> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            int value;
            if (i == 0) {
                value = c[i] % maxIndex;
            } else {
                value = (list.get(i - 1) + c[i] % maxIndex) % maxIndex;
                while (list.contains(value)) {
                    if (value < maxIndex - 1) {
                        value++;
                    } else {
                        value = 0;
                    }
                }
            }
            list.add(value);
        }
        return list.toArray(new Integer[length]);
    }

    private String encryptByMd5Source(final String md5Source, Object secretKey,
            final long staticKey) {
        if (secretKey == null) {
            secretKey = "";
        }
        final String keyMd5 = Md5Encrypter.encrypt32(staticKey + secretKey.toString());
        final char[] keyChars = keyMd5.toCharArray();
        final char[] sourceChars = md5Source.toLowerCase().toCharArray();
        final int length = keyChars.length + sourceChars.length;
        final char[] c = new char[length];
        final Integer[] sourceCharIndexes = getMd5SourceCharIndexes(staticKey, length);
        for (int i = 0; i < sourceChars.length; i++) {
            c[sourceCharIndexes[i]] = sourceChars[i];
        }
        int index = 0;
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 0) {
                c[i] = keyChars[index++];
            }
        }
        return new String(c);
    }

    public boolean validateByMd5Source(final String encryptedText, final String md5Source,
            final Object secretKey) {
        if (md5Source.length() != MD5_ENCRYPT_LENGTH) {
            return false;
        }
        final String encrptedResult = encryptByMd5Source(md5Source, secretKey, this.staticKey);
        return encryptedText.equalsIgnoreCase(encrptedResult);
    }

    public String getMd5Source(final String encryptedText) {
        if (encryptedText.length() != ENCRYPTED_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    "The length of encrypted text must be " + ENCRYPTED_TEXT_LENGTH);
        }
        final Integer[] indexes = getMd5SourceCharIndexes(this.staticKey, encryptedText.length());
        final char[] c = new char[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            c[i] = encryptedText.charAt(indexes[i]);
        }
        return new String(c);
    }

}
