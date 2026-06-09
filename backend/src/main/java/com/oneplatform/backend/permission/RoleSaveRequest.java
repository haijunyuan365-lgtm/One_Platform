package com.oneplatform.backend.permission;

import java.util.List;

public record RoleSaveRequest(
        String code,
        String name,
        String description,
        Integer sort,
        Integer status,
        List<Long> menuIds
) {
}
