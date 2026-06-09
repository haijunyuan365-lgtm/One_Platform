package com.oneplatform.backend.project;

import java.time.LocalDateTime;

public record ProjectAddressRecord(
        Long id,
        Long projectId,
        String name,
        String type,
        String url,
        Integer isDefault,
        Integer isDetection,
        Integer sort,
        Integer status,
        LocalDateTime updatedAt
) {
}
