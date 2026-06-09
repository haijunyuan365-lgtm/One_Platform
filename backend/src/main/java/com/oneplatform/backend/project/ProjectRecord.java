package com.oneplatform.backend.project;

import java.time.LocalDateTime;

public record ProjectRecord(
        Long id,
        String name,
        String shortName,
        Long logoFileId,
        String logoUrl,
        String category,
        String tags,
        String description,
        Long maintainerId,
        String maintainerName,
        Integer sort,
        Integer enabled,
        String status,
        LocalDateTime lastCheckTime,
        Integer responseTime,
        String abnormalReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
