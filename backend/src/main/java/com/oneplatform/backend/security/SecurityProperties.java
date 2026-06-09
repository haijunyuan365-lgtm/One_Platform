package com.oneplatform.backend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oneplatform.security")
public record SecurityProperties(String jwtSecret, long accessTokenMinutes) {
}
