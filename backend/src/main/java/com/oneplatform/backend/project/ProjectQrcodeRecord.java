package com.oneplatform.backend.project;

import java.time.LocalDateTime;

public record ProjectQrcodeRecord(
        Long id,
        Long projectId,
        String projectName,
        String name,
        Long fileId,
        String imageUrl,
        String audience,
        String description,
        Integer status,
        LocalDateTime updatedAt
) {
}
