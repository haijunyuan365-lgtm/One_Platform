package com.oneplatform.backend.permission;

import java.time.LocalDateTime;

public record MenuRecord(
        Long id,
        Long parentId,
        String name,
        String title,
        String type,
        String path,
        String component,
        String permission,
        String icon,
        Integer sort,
        Integer status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
