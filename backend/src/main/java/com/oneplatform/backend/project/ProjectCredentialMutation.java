package com.oneplatform.backend.project;

import java.time.LocalDate;

public record ProjectCredentialMutation(
        Long projectId,
        String name,
        String username,
        String passwordCipher,
        String passwordMasked,
        String environment,
        String description,
        LocalDate expireDate,
        Integer status
) {
}
