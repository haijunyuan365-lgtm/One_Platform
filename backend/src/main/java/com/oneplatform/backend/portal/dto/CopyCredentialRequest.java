package com.oneplatform.backend.portal.dto;

import jakarta.validation.constraints.NotBlank;

public record CopyCredentialRequest(
        @NotBlank String field
) {
}
