package com.oneplatform.backend.project;

public record ProjectInstructionMutation(
        String browserRequirement,
        String vpnRequirement,
        String notes,
        String maintainer,
        String contactPhone
) {
}
