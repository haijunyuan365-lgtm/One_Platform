package com.oneplatform.backend.project;

import java.time.LocalDate;

public record ProjectCredentialSaveRequest(
        Long id,
        Long projectId,
        String name,
        String username,
        String password,
        String environment,
        String description,
        LocalDate expireDate,
        Integer status
) {
}
