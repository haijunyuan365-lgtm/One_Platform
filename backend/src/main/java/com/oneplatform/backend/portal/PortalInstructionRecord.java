package com.oneplatform.backend.portal;

public record PortalInstructionRecord(
        String browserRequirement,
        String vpnRequirement,
        String notes,
        String maintainer,
        String contactPhone
) {
}
