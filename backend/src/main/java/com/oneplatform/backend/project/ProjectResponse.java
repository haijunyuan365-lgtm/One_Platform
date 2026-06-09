package com.oneplatform.backend.project;

import java.util.List;

public record ProjectResponse(
        Long id,
        String name,
        String shortName,
        String logo,
        String category,
        List<String> tags,
        String description,
        Long maintainerId,
        String maintainer,
        Integer sort,
        Integer enabled,
        String status,
        String lastCheckTime,
        Integer responseTime,
        String abnormalReason,
        String createTime,
        String updateTime
) {
}
