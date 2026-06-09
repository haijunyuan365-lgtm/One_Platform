package com.oneplatform.backend.permission;

public record MenuSaveRequest(
        Long parentId,
        String name,
        String title,
        String type,
        String path,
        String component,
        String permission,
        String icon,
        Integer sort,
        Integer status
) {
}
