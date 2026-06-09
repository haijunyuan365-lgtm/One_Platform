package com.oneplatform.backend.permission;

import java.util.List;

public record MenuResponse(
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
        String createTime,
        String updateTime,
        List<MenuResponse> children
) {
}
