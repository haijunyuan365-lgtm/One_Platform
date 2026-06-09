package com.oneplatform.backend.organization;

import java.time.LocalDateTime;

public record DepartmentRecord(
        Long id,
        Long parentId,
        String name,
        String code,
        String leader,
        String phone,
        Integer sort,
        Integer status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
