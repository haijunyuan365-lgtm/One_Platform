package com.oneplatform.backend.portal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oneplatform.portal")
public record PortalProperties(
        boolean allowDevFallbackUser,
        Long devFallbackUserId
) {
}
