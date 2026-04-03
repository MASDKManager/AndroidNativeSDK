package com.opn.nativeflow;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESHelper {

    private final SecretKeySpec keySpec;
    private final IvParameterSpec ivSpec;

    public AESHelper(String encryptionKey) {
        byte[] keyBytes = encryptionKey.getBytes();
        keySpec = new SecretKeySpec(keyBytes, "AES");
        // IV = first 16 characters of the encryption key
        ivSpec = new IvParameterSpec(encryptionKey.substring(0, 16).getBytes());
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decoded = Base64.decode(encryptedText, Base64.NO_WRAP);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }
}
