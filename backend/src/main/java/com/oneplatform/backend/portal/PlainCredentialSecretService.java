package com.oneplatform.backend.portal;

public class PlainCredentialSecretService implements CredentialSecretService {

    @Override
    public String encrypt(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return "";
        }
        return "plain:" + rawPassword;
    }

    @Override
    public String decrypt(String passwordCipher) {
        if (passwordCipher == null || passwordCipher.isBlank()) {
            return "";
        }
        if (passwordCipher.startsWith("plain:")) {
            return passwordCipher.substring("plain:".length());
        }
        return passwordCipher;
    }
}
