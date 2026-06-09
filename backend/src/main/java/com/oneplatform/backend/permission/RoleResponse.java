package com.oneplatform.backend.permission;

import java.util.List;

public record RoleResponse(
        Long id,
        String code,
        String name,
        String description,
        List<Long> menuIds,
        Long userCount,
        Integer sort,
        Integer status,
        String createTime,
        String updateTime
) {
}
