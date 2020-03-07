package com.example.skillboxchat;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static final String pass = "skillbox";
    private static SecretKeySpec keySpec;

    static {
        // SHA-256
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] bytes = pass.getBytes();
            sha256.update(bytes);
            byte[] key = sha256.digest(); // Хэш от слова skillbox
            keySpec = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String text) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = aesCipher.doFinal(text.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String encoded) throws Exception {
        byte[] encrypted = Base64.decode(encoded, Base64.DEFAULT);
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = aesCipher.doFinal(encrypted);
        return new String(decrypted, "UTF-8");
    }
}
