package com.oneplatform.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oneplatform.init")
public record InitProperties(String adminPassword) {
}
