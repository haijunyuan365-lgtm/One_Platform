package com.oneplatform.backend.organization;

public record UserQuery(
        String keyword,
        Long departmentId,
        Integer status,
        long page,
        long pageSize
) {
}
