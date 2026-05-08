package com.pesa.common.api;

import java.time.Instant;

public final class ApiResponses {

    private ApiResponses() {}

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now());
    }

    public static ApiResponse<Object> error(String message) {
        return error(message, message);
    }

    public static ApiResponse<Object> error(String developerMessage, String userMessage) {
        return new ApiResponse<>(developerMessage, userMessage, Instant.now());
    }
}
