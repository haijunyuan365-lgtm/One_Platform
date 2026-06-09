package com.oneplatform.backend.project;

public record ProjectAddressResponse(
        Long id,
        Long projectId,
        String name,
        String type,
        String url,
        Integer isDefault,
        Integer isDetection,
        Integer sort,
        Integer status
) {
}
