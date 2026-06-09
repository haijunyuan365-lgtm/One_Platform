package com.oneplatform.backend.organization;

import java.util.List;

public record DepartmentResponse(
        Long id,
        Long parentId,
        String name,
        String code,
        String type,
        String leader,
        String phone,
        Integer sort,
        Integer status,
        String createTime,
        String updateTime,
        List<DepartmentResponse> children
) {
}
