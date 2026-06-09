package com.oneplatform.backend.auth.dto;

public record LoginResponse(String token, String refreshToken, UserInfoResponse user) {
}
