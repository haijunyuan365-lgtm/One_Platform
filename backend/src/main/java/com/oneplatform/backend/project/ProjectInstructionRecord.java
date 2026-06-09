package com.oneplatform.backend.project;

import java.time.LocalDateTime;

public record ProjectInstructionRecord(
        Long id,
        Long projectId,
        String browserRequirement,
        String vpnRequirement,
        String notes,
        String maintainer,
        String contactPhone,
        LocalDateTime updatedAt
) {
}
