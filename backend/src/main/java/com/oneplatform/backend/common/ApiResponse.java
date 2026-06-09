package com.oneplatform.backend.common;

public record ApiResponse<T>(
        int code,
        boolean success,
        String message,
        T data,
        long timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, true, "success", data, System.currentTimeMillis());
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, true, message, data, System.currentTimeMillis());
    }

    public static <T> ApiResponse<T> failure(int code, String message) {
        return new ApiResponse<>(code, false, message, null, System.currentTimeMillis());
    }
}
