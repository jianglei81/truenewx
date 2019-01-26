package org.truenewx.core.encrypt;

import java.util.Random;

public class Base64xEncrypter implements KeyEncrypter {

    public final static Base64xEncrypter INSTANCE = new Base64xEncrypter();

    private Base64xEncrypter() {
    }

    @Override
    public String encrypt(final Object source, final Object key) {
        String random = String.valueOf(new Random().nextInt(32000));
        String encryptText = Md5Encrypter.encrypt32(random);
        int j = 0;
        String temp = "";
        char encryptTextArray[] = encryptText.toCharArray();
        String text = source.toString();
        char textChar[] = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            j = j == encryptTextArray.length ? 0 : j;
            char c1 = textChar[i];
            char c2 = encryptTextArray[j++];
            char c3 = (char) (c1 ^ c2);
            char c4 = encryptTextArray[j - 1];
            temp += c4 + "" + c3;
        }
        return Base64Encrypter.INSTANCE.encrypt(calculate(temp, key));
    }

    public String decrypt(String encryptedText, final Object key) {
        encryptedText = calculate(Base64Encrypter.INSTANCE.decrypt(encryptedText), key);
        if (encryptedText == null) {
            return null;
        }
        String text = "";
        char encryptedTextChar[] = encryptedText.toCharArray();
        for (int i = 0; i < encryptedText.length(); i++) {
            text += (char) (encryptedTextChar[i] ^ encryptedTextChar[++i]);
        }
        return text;
    }

    private static String calculate(final String text, final Object key) {
        if (text == null) {
            return null;
        }
        String keyString = Md5Encrypter.encrypt32(key);
        int j = 0;
        String temp = "";
        char encryptKeyChar[] = keyString.toCharArray();
        char textChar[] = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            j = j == keyString.length() ? 0 : j;
            char c = (char) (textChar[i] ^ encryptKeyChar[j++]);
            temp = temp + c;
        }
        return temp;
    }
}
