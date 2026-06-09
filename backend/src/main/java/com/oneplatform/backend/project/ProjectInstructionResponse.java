package com.oneplatform.backend.project;

public record ProjectInstructionResponse(
        Long projectId,
        String browserRequirement,
        String vpnRequirement,
        String notes,
        String maintainer,
        String contactPhone,
        String updateTime
) {
}
