package com.oneplatform.backend.portal.dto;

public record PortalProjectQuery(
        String keyword,
        String category,
        String status
) {
}
