package com.oneplatform.backend.portal.dto;

import java.util.List;

public record PortalProjectDetailResponse(
        ProjectCard project,
        List<ProjectAddress> addresses,
        List<ProjectCredential> credentials,
        List<ProjectQrcode> qrcodes,
        ProjectInstruction instruction,
        boolean canViewPassword,
        boolean canCopyPassword,
        boolean canViewQrcode
) {

    public record ProjectCard(
            Long id,
            String name,
            String shortName,
            String logo,
            String category,
            List<String> tags,
            String description,
            String maintainer,
            String status,
            String lastCheckTime,
            Integer responseTime,
            String abnormalReason
    ) {
    }

    public record ProjectAddress(
            Long id,
            String name,
            String type,
            String url,
            Integer isDefault
    ) {
    }

    public record ProjectCredential(
            Long id,
            String name,
            String username,
            String passwordMasked,
            String environment,
            String description
    ) {
    }

    public record ProjectQrcode(
            Long id,
            String name,
            String image,
            String audience,
            String description
    ) {
    }

    public record ProjectInstruction(
            String browserRequirement,
            String vpnRequirement,
            String notes,
            String maintainer,
            String contactPhone
    ) {
    }
}
