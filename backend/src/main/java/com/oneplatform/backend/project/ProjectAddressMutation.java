package com.oneplatform.backend.project;

public record ProjectAddressMutation(
        Long id,
        String name,
        String type,
        String url,
        Integer isDefault,
        Integer isDetection,
        Integer sort,
        Integer status
) {
}
