package com.oneplatform.backend.project;

public record ProjectQrcodeResponse(
        Long id,
        Long projectId,
        String projectName,
        String name,
        String image,
        String audience,
        String description,
        Integer status,
        String updateTime
) {
}
