package com.oneplatform.backend.project;

public record ProjectQrcodeSaveRequest(
        Long id,
        Long projectId,
        String name,
        String image,
        String audience,
        String description,
        Integer status
) {
}
