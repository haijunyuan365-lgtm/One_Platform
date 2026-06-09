package com.oneplatform.backend.portal;

public record PortalQrcodeRecord(
        Long id,
        String name,
        String imageUrl,
        String audience,
        String description
) {
}
