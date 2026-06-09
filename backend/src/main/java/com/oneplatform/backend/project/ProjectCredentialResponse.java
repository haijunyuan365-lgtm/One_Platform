package com.oneplatform.backend.project;

import java.time.LocalDate;

public record ProjectCredentialResponse(
        Long id,
        Long projectId,
        String projectName,
        String name,
        String username,
        String passwordMasked,
        String environment,
        String description,
        LocalDate expireDate,
        Integer status,
        String updateTime
) {
}
