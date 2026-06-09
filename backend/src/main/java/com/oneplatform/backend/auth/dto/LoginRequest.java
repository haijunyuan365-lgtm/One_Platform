package com.oneplatform.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        String userName,
        String username,
        @NotBlank String password,
        String captchaCode,
        String captchaId
) {

    public String account() {
        if (userName != null && !userName.isBlank()) {
            return userName;
        }
        return username;
    }
}
