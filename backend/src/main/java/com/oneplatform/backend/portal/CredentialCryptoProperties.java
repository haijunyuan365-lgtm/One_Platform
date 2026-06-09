package com.oneplatform.backend.portal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oneplatform.credential")
public record CredentialCryptoProperties(
        String secretKey
) {
}
