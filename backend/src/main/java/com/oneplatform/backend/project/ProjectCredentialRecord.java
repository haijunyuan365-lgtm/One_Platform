package com.oneplatform.backend.project;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectCredentialRecord(
        Long id,
        Long projectId,
        String projectName,
        String name,
        String username,
        String passwordCipher,
        String passwordMasked,
        String environment,
        String description,
        LocalDate expireDate,
        Integer status,
        LocalDateTime updatedAt
) {
}
