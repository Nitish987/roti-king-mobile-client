package com.rotiking.client.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES128 {
    static {
        System.loadLibrary("client");
    }

    public static String NATIVE_ENCRYPTION_KEY = "";

    public native static String getNativeEncryptionKey();

    public static void loadEncryptionKey() {
        NATIVE_ENCRYPTION_KEY = new String(Base64.getDecoder().decode(getNativeEncryptionKey())).trim();
    }


    public static String encrypt(String key, String plaintext) {
        try {
            byte[] initVector = new byte[16];
            (new Random()).nextBytes(initVector);
            IvParameterSpec iv = new IvParameterSpec(initVector);

            SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);

            byte[] cipherBytes = cipher.doFinal(plaintext.getBytes());

            byte[] messageBytes = new byte[initVector.length + cipherBytes.length];

            System.arraycopy(initVector, 0, messageBytes, 0, 16);
            System.arraycopy(cipherBytes, 0, messageBytes, 16, cipherBytes.length);

            return Base64.getEncoder().encodeToString(messageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String key, String ciphertext) {
        try {
            byte[] cipherBytes = Base64.getDecoder().decode(ciphertext);

            byte[] initVector = Arrays.copyOfRange(cipherBytes, 0, 16);

            byte[] messageBytes = Arrays.copyOfRange(cipherBytes, 16, cipherBytes.length);

            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);

            byte[] byte_array = cipher.doFinal(messageBytes);

            return new String(byte_array, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
