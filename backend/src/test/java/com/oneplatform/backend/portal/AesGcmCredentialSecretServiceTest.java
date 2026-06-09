package com.oneplatform.backend.portal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AesGcmCredentialSecretServiceTest {

    @Test
    void decryptsVersionedAesGcmCipherText() {
        CredentialCryptoProperties properties = new CredentialCryptoProperties(
                "T25lUGxhdGZvcm0yMDI2Q3JlZGVudGlhbEtleTAwMDE="
        );
        AesGcmCredentialSecretService service = new AesGcmCredentialSecretService(properties);

        String decrypted = service.decrypt("v1:wHHNdw18fLiqfpva:8Qoq8RODFNLlrIi+D+1da6/u0Et51Nk66/N4MK6x");

        assertThat(decrypted).isEqualTo("DemoPortal2026");
    }

    @Test
    void encryptsPasswordToVersionedCipherTextThatCanBeDecrypted() {
        CredentialCryptoProperties properties = new CredentialCryptoProperties(
                "T25lUGxhdGZvcm0yMDI2Q3JlZGVudGlhbEtleTAwMDE="
        );
        AesGcmCredentialSecretService service = new AesGcmCredentialSecretService(properties);

        String encrypted = service.encrypt("Secret#2026");

        assertThat(encrypted).startsWith("v1:");
        assertThat(encrypted).doesNotContain("Secret#2026");
        assertThat(service.decrypt(encrypted)).isEqualTo("Secret#2026");
    }
}
