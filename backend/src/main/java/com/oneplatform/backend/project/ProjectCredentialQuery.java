package com.oneplatform.backend.project;

public record ProjectCredentialQuery(
        Long projectId,
        String environment,
        Integer status
) {
}
