package com.pesa.controller;

import com.pesa.common.exception.UnauthorizedException;
import com.pesa.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.pesa.common.api.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final JwtTokenProvider tokenProvider;

    // Called by AuthRepository.refreshToken()
    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshViaBody(@RequestBody TokenRefreshBody body) {
        return doRefresh(body.refreshToken());
    }

    // Called by ApiClient.refreshToken() and the auth interceptor
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshLegacy(@RequestBody TokenRefreshBody body) {
        return doRefresh(body.refreshToken());
    }

    private ResponseEntity<?> doRefresh(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        String phone = tokenProvider.getPhoneNumberFromToken(refreshToken);
        String newAccess = tokenProvider.generateAccessToken(userId, phone);
        String newRefresh = tokenProvider.generateRefreshToken(userId, phone);
        return ResponseEntity.ok(
                ApiResponses.success("Token refreshed", Map.of("accessToken", newAccess, "refreshToken", newRefresh)));
    }
}

record TokenRefreshBody(String refreshToken) {
}
