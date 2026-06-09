package com.oneplatform.backend.permission;

import java.util.List;

public record RoleMutation(
        String code,
        String name,
        String description,
        Integer sort,
        Integer status,
        List<Long> menuIds
) {
}
