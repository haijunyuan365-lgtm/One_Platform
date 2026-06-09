package com.oneplatform.backend.file;

import java.time.LocalDateTime;

public record PlatformFileRecord(
        Long id,
        String originName,
        String storageName,
        String storagePath,
        String url,
        String contentType,
        long size,
        String bizType,
        Long uploaderId,
        LocalDateTime uploadedAt
) {
}
