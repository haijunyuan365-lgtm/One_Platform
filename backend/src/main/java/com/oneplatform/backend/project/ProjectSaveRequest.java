package com.oneplatform.backend.project;

import java.util.List;

public record ProjectSaveRequest(
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
        String status
) {
}
