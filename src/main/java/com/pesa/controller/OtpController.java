package com.pesa.controller;

import com.pesa.dto.OtpType;
import com.pesa.dto.OtpVerifyRequest;
import com.pesa.entity.User;
import com.pesa.repository.UserRepository;

import com.pesa.service.OtpStoreService;
import com.pesa.util.JwtTokenProvider;
import com.pesa.util.OtpGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.pesa.common.api.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
@Slf4j
public class OtpController {

    private final JwtTokenProvider tokenProvider;

    private final OtpGenerator otpGenerator;
    private final OtpStoreService otpStoreService;
    private final UserRepository userRepository;

    @PostMapping("/request")
    public ResponseEntity<?> requestOtp(@Valid @RequestBody OtpRequestBody body) {

        try {
            log.debug("Starting OTP generation for phone: {}", body.getPhoneNumber());
            String otp = otpGenerator.generateOtp();
            log.debug("OTP generated: {}", otp);

            // TODO: Add Bulk SMS sending logic here using a service

            log.debug("About to store OTP in Redis");
            otpStoreService.saveOtp(body.getPhoneNumber(), otp);
            log.debug("OTP stored successfully in Redis");
            log.info("OTP for {}: {}", body.getPhoneNumber(), otp);
            return ResponseEntity.ok(ApiResponses.success("OTP sent", Map.of("otp", otp)));
        } catch (Exception e) {
            log.error("Exception in OTP request handler", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error("Failed to request OTP: " + e.getMessage()));
        }

    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerifyBody body) {

        OtpVerifyRequest otpVerifyRequest;

        try {

            otpVerifyRequest = OtpVerifyRequest.builder()
                    .phoneNumber(body.getPhoneNumber())
                    .code(body.getOtp())
                    .type(body.getType())
                    .build();

            otpStoreService.verifyOtp(
                    otpVerifyRequest.getPhoneNumber(),
                    otpVerifyRequest.getCode());

            switch (otpVerifyRequest.getType()) {
                case LOGIN:
                    User user = userRepository
                            .findByPhoneNumber(otpVerifyRequest.getPhoneNumber())
                            .orElseGet(() -> {
                                User newUser = new User();
                                newUser.setPhoneNumber(otpVerifyRequest.getPhoneNumber());
                                return userRepository.save(newUser);
                            });

                    String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getPhoneNumber());
                    String refreshToken = tokenProvider.generateRefreshToken(user.getId(), user.getPhoneNumber());

                    return ResponseEntity.ok(ApiResponses.success("OTP verified",
                            new AuthResponse(accessToken, refreshToken, Map.of(
                                    "userId", user.getId(),
                                    "phoneNumber", user.getPhoneNumber()))));

                // TODO: Add other OTP types like registration, password reset, etc.

                default:
                    log.warn("Unhandled OTP type: {}", otpVerifyRequest.getType());
                    return ResponseEntity.badRequest()
                            .body(ApiResponses.error("Unhandled OTP type: " + otpVerifyRequest.getType()));
            }

        } catch (Exception e) {
            log.error("Error verifying OTP", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error("Failed to verify OTP: " + e.getMessage()));
        }

    }
}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class OtpRequestBody {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{9,15}$", message = "Phone number must be 9-15 digits without + symbol")
    private String phoneNumber;
}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class OtpVerifyBody {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{9,15}$", message = "Phone number must be 9-15 digits without + symbol")
    private String phoneNumber;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 digits")
    private String otp;

    @NotNull(message = "OTP type is required")
    private OtpType type;
}

record AuthResponse(String token, String refreshToken, Map<String, Object> user) {
}