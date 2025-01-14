package com.RedAlien.RedAlienShop.Helper;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static final String KEY = "RedAlienShop Key";
    private static final String TAG = "Crypto";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int AUTH_TAG_LEN = 128;

    public static SecretKeySpec generateKEY()  {
        return new SecretKeySpec(KEY.getBytes(),  ALGORITHM);
    }

    public static String encrypt(String plainText, SecretKeySpec secretKey) throws Exception {
        // 랜덤 IV값 생성 : Android Guideline 에서는 SecureRandom을 사용하여서 IV값을 생성하라고 명시되어 있다
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // // Encrypt모드로 Cipher 객체 생성 및 초기화
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(AUTH_TAG_LEN, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec, secureRandom);

        // Cipher가 초기화된 방식에 따라, 암호화/복호화 실행
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // iv 길이 + encrypted 길이의 buffer 생성 및 할당
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.encodeToString(combined, Base64.DEFAULT);
        // IV를 합해서 같이 보내는 이유는 Decrypt 할 때에도 동일한 IV가 필요하기 때문이다
    }

    public static String decypt(String encryptText, SecretKeySpec secretKey) throws Exception {
        byte[] combined = Base64.decode(encryptText, Base64.DEFAULT);

        // IV 추출
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // EncryptedBytes 추출
        byte[] encryptedText = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encryptedText, 0, (combined.length - iv.length));

        // 인증 태그 검사 및 복호화
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(AUTH_TAG_LEN, iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedText);

        return new String(decryptedBytes);
    }
}
