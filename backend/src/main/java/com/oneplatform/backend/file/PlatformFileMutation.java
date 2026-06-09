package com.oneplatform.backend.file;

public record PlatformFileMutation(
        String originName,
        String storageName,
        String storagePath,
        String url,
        String contentType,
        long size,
        String bizType,
        Long uploaderId
) {
}
