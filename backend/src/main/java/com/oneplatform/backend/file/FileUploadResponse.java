package com.oneplatform.backend.file;

public record FileUploadResponse(
        Long id,
        String originName,
        String url,
        String contentType,
        long size,
        String bizType,
        String uploadedAt
) {
}
