package com.oneplatform.backend.portal;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.oneplatform.backend.common.BusinessException;

import org.springframework.stereotype.Service;

@Service
public class AesGcmCredentialSecretService implements CredentialSecretService {

    private static final String PREFIX = "v1:";
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_IV_BYTES = 12;

    private final byte[] key;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesGcmCredentialSecretService(CredentialCryptoProperties properties) {
        this.key = decodeKey(properties.secretKey());
    }

    @Override
    public String encrypt(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return "";
        }
        try {
            byte[] iv = new byte[GCM_IV_BYTES];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(rawPassword.getBytes(StandardCharsets.UTF_8));
            return PREFIX
                    + Base64.getEncoder().encodeToString(iv)
                    + ":"
                    + Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException ex) {
            throw new BusinessException(500, "凭据加密失败");
        }
    }

    @Override
    public String decrypt(String passwordCipher) {
        if (passwordCipher == null || passwordCipher.isBlank()) {
            return "";
        }
        if (!passwordCipher.startsWith(PREFIX)) {
            throw new BusinessException(500, "凭据密文格式不正确");
        }
        String[] parts = passwordCipher.split(":", 3);
        if (parts.length != 3) {
            throw new BusinessException(500, "凭据密文格式不正确");
        }
        try {
            byte[] iv = Base64.getDecoder().decode(parts[1]);
            byte[] encrypted = Base64.getDecoder().decode(parts[2]);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new BusinessException(500, "凭据解密失败");
        }
    }

    private static byte[] decodeKey(String secretKey) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("Credential secret key must not be blank");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Credential secret key must be 16, 24, or 32 bytes after Base64 decoding");
        }
        return keyBytes;
    }
}
