package com.oneplatform.backend.portal;

public interface CredentialSecretService {

    String encrypt(String rawPassword);

    String decrypt(String passwordCipher);
}
