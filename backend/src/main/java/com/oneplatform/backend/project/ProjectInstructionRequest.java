package com.oneplatform.backend.project;

public record ProjectInstructionRequest(
        Long projectId,
        String browserRequirement,
        String vpnRequirement,
        String notes,
        String maintainer,
        String contactPhone
) {
}
