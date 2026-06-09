package com.oneplatform.backend.project;

public record ProjectQrcodeMutation(
        Long projectId,
        String name,
        Long fileId,
        String imageUrl,
        String audience,
        String description,
        Integer status
) {
}
