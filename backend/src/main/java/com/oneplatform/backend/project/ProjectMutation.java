package com.oneplatform.backend.project;

public record ProjectMutation(
        String name,
        String shortName,
        Long logoFileId,
        String logoUrl,
        String category,
        String tags,
        String description,
        Long maintainerId,
        String maintainerName,
        Integer sort,
        Integer enabled,
        String status
) {
}
