package com.oneplatform.backend.portal;

import java.time.LocalDateTime;

public record PortalProjectRecord(
        Long id,
        String name,
        String shortName,
        String logoUrl,
        String category,
        String tags,
        String description,
        String maintainerName,
        String status,
        LocalDateTime lastCheckTime,
        Integer responseTime,
        String abnormalReason
) {
}
