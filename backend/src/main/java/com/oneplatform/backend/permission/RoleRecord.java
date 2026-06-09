package com.oneplatform.backend.permission;

import java.time.LocalDateTime;

public record RoleRecord(
        Long id,
        String code,
        String name,
        String description,
        Integer sort,
        Integer status,
        Integer isSystem,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
