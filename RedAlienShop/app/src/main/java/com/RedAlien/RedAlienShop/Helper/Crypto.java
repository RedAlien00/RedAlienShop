package com.RedAlien.RedAlienShop.Helper;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static final String KEY = "RedAlienShop Secret Key";
    private static final String TAG = "Crypto";
    private static final String ALGORITHM = "BLOWFISH";
    private static final String TRANSFORMATION = "BLOWFISH/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 8;
    private static final int BLOWFISH_KEY_SIZE = 256;


    public static SecretKeySpec generateKEY() throws NoSuchAlgorithmException {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
//        keyGenerator.init(BLOWFISH_KEY_SIZE, new SecureRandom());
        return new SecretKeySpec(KEY.getBytes(), ALGORITHM);
    }

    public static String encrypt(String plainText, SecretKeySpec secretKey) throws Exception {
        byte[] iv = new byte[IV_LENGTH]; // BLOWFISH는 64bit = 8bytes 블록 크기를 사용하기 때문에
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION); // 수행할 작업 명시하여, Cipher 객체 생성
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv)  );  // Encrypt모드, Key로 Cipher 초기화
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)); // Cipher가 초기화된 방식에 따라, 암호화/복호화 실행

        byte[] combined = new byte[iv.length + encryptedBytes.length]; // iv 길이 + encrypted 길이의 buffer 생성
        // iv의 0번째부터 iv길이만큼 복사하여 combined의 0번째에 붙여넣기
        System.arraycopy(iv, 0, combined, 0, iv.length);
        // encryptedBytes의 0번째부터 encryptedBytes의 길이만큼 복사하여, combined의 iv.length번째부터 붙여넣기
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.encodeToString(combined, Base64.DEFAULT);

        // IV를 합해서 같이 보내는 이유는 Decrypt 할 때에도 동일한 IV가 필요하기 때문이다
        // Android Guideline 에서는 SecureRandom을 사용하여서 IV값을 생성하라고 명시되어 있다
        // IV값을 전역변수로 먼저 선언하였을 경우, IV를 합치지 않고, DecryptBytes만 보내면 된다
    }

    public static String decypt(String encryptText, SecretKeySpec secretKey) throws Exception {
        byte[] combined = Base64.decode(encryptText, Base64.DEFAULT);

        // IV 추출
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // EncryptedBytes 추출
        byte[] encryptedText = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encryptedText, 0, (combined.length - iv.length));

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedText);

        return new String(decryptedBytes);
    }
}
