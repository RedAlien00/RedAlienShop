package com.RedAlien.RedAlienShop.Helper;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

// 참고 : Android Document - Android 키 저장소 시스템
// https://developer.android.com/privacy-and-security/keystore?hl=ko#java
public class Crypto {
    private static final String TAG = "Crypto";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEYSTORE_ALIAS = "myAESKey";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;    // AES는 16byte나, GCM은 12byte 사용
    private static final int tLEN = 128;

    // AndroidKeyStore에 BlowFish 알고리즘으로 생성한 키를 저장할 경우, 예외 발생
    // java.security.KeyStoreException: java.lang.IllegalArgumentException: Unsupported secret key algorithm: Blowfish
    public static SecretKey generateKEY() {
        SecretKey secretKey = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            if (keyStore.isKeyEntry(KEYSTORE_ALIAS)){
                Log.i(TAG, "Alias : " + KEYSTORE_ALIAS);
                secretKey = (SecretKey) keyStore.getKey(KEYSTORE_ALIAS, null);
                return secretKey;
            }

            // 참고 - https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec#example:-aes-key-for-encryptiondecryption-in-gcm-mode
            // 비대칭키 생성 클래스인 KeyPairGenerator, 대칭키 생성 클래스인 keyGenerator를 사용하여
            // 키를 생성할 때, 키의 속성들을 정의하는 클래스, 생성된 키는 AndroidKeyStore에 자동 저장됨
            // 키 속성 : 키의 사용 목적(암/복호화), 블럭모드, 유효기간 등...
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();
            // 패딩을 ENCRYPTION_PADDING_PKCS7로 설정할 경우, 예외 발생
            // java.lang.NullPointerException: Attempt to get length of null array
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(keyGenParameterSpec, new SecureRandom());
            secretKey = keyGenerator.generateKey();

            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                Log.i(TAG, "Alias : " + aliases.nextElement());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return secretKey;
    }

    public static SecretKey loadKey(){
        SecretKey secretKey = null;
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            secretKey = (SecretKey) keyStore.getKey(KEYSTORE_ALIAS, null);
        } catch (Exception e){
            e.printStackTrace();
        }
        return secretKey;
    }

    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        // Encrypt모드로 Cipher 초기화
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());

        // Cipher가 초기화된 방식에 따라, 암호화/복호화 실행 및 IV값 가져오기
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] iv = cipher.getIV();

        // iv 길이 + encrypted 길이의 buffer 생성 및 할당
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.encodeToString(combined, Base64.DEFAULT);

        // IV를 합해서 같이 보내는 이유는 Decrypt 할 때에도 동일한 IV가 필요하기 때문이다
        // Android Guideline 에서는 SecureRandom을 사용하여서 매 번 IV값을 생성하라고 명시되어 있다
    }

    public static String decypt(String encryptText, SecretKey secretKey) throws Exception {
        byte[] combined = Base64.decode(encryptText, Base64.DEFAULT);

        // IV 추출
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // EncryptedBytes 추출
        byte[] encryptedText = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encryptedText, 0, (combined.length - iv.length));

        // 복호화
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(tLEN, iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedText);

        return new String(decryptedBytes);
    }
}
