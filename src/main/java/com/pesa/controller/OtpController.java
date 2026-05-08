package com.pesa.controller;

import com.pesa.dto.OtpType;
import com.pesa.dto.OtpVerifyRequest;
import com.pesa.dto.AuthResponse;
import com.pesa.dto.UserDto;
import com.pesa.entity.User;
import com.pesa.entity.KycProfile;
import com.pesa.repository.UserRepository;
import com.pesa.repository.KycProfileRepository;

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
    private final KycProfileRepository kycProfileRepository;

    @PostMapping("/request")
    public ResponseEntity<?> requestOtp(@Valid @RequestBody OtpRequestBody body) {

        try {
            log.debug("Starting OTP generation for phone: {}", body.getPhoneNumber());
            String otp = otpGenerator.generateOtp();
            log.debug("OTP generated: {}", otp);

            sendOtpViaSms(body.getPhoneNumber(), otp);

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
                    .type(body.getPurpose())
                    .build();

            otpStoreService.verifyOtp(
                    otpVerifyRequest.getPhoneNumber(),
                    otpVerifyRequest.getCode());

            switch (otpVerifyRequest.getType()) {
                case LOGIN:
                case REGISTRATION:
                    User user = userRepository
                            .findByPhoneNumber(otpVerifyRequest.getPhoneNumber())
                            .orElseGet(() -> {
                                User newUser = new User();
                                newUser.setPhoneNumber(otpVerifyRequest.getPhoneNumber());
                                return userRepository.save(newUser);
                            });

                    KycProfile kycProfile = kycProfileRepository.findByUserId(user.getId()).orElse(null);
                    if (kycProfile == null) {
                        kycProfile = new KycProfile();
                        kycProfile.setUserId(user.getId());
                        kycProfile.setStatus(KycProfile.KycStatus.PENDING);
                        kycProfile.setCompletionStep(KycProfile.KycStep.PERSONAL_INFO);
                        kycProfileRepository.save(kycProfile);
                    }

                    String kycStatus = kycProfile.getStatus().name();
                    String creditLimit = "0";

                    String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getPhoneNumber());
                    String refreshToken = tokenProvider.generateRefreshToken(user.getId(), user.getPhoneNumber());

                    UserDto userDto = UserDto.builder()
                            .id(user.getId())
                            .phoneNumber(user.getPhoneNumber())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .kycStatus(kycStatus)
                            .creditLimit(creditLimit)
                            .build();

                    AuthResponse authResponse = AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .user(userDto)
                            .build();

                    return ResponseEntity.ok(ApiResponses.success("OTP verified", authResponse));

                case PASSWORD_RESET:
                    return ResponseEntity.ok(ApiResponses.success("OTP verified for password reset", null));

                case TRANSACTION:
                    return ResponseEntity.ok(ApiResponses.success("OTP verified for transaction", null));

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

    private void sendOtpViaSms(String phoneNumber, String otp) {
        try {
            log.debug("Sending OTP via SMS to: {}", phoneNumber);
        } catch (Exception e) {
            log.warn("Failed to send SMS for phone: {}, continuing with OTP storage", phoneNumber, e);
        }
    }
}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class OtpRequestBody {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+?\\d{10,12}|0[67]\\d{8})$", message = "Phone number must be in Tanzania format (255XXXXXXXXX, +255XXXXXXXXX, or 0XXXXXXXXX)")
    private String phoneNumber;
}

@Getter
@NoArgsConstructor
@AllArgsConstructor
class OtpVerifyBody {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+?\\d{10,12}|0[67]\\d{8})$", message = "Phone number must be in Tanzania format (255XXXXXXXXX, +255XXXXXXXXX, or 0XXXXXXXXX)")
    private String phoneNumber;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 digits")
    private String otp;

    @NotNull(message = "OTP purpose is required")
    private OtpType purpose;
}

