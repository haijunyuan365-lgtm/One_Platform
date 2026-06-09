package com.oneplatform.backend.portal;

public record PortalCredentialRecord(
        Long id,
        Long projectId,
        String name,
        String username,
        String passwordCipher,
        String passwordMasked,
        String environment,
        String description,
        Integer status
) {
}
