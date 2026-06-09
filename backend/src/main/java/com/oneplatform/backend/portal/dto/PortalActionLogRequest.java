package com.oneplatform.backend.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PortalActionLogRequest(
        @NotNull Long projectId,
        @NotBlank String action,
        @NotBlank String target
) {
}
