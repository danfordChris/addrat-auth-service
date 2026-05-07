package com.pesa.controller;

import com.pesa.dto.ApiResponse;
import com.pesa.dto.KycRequest;
import com.pesa.dto.KycResponse;
import com.pesa.entity.KycProfile;
import com.pesa.service.KycService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final KycService kycService;

    public UserController(KycService kycService) {
        this.kycService = kycService;
    }

    @GetMapping("/me/kyc")
    public ResponseEntity<ApiResponse<KycResponse>> getKycProfile(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);
            KycResponse response = mapToKycResponse(profile);
            return ResponseEntity.ok(new ApiResponse<>(true, "KYC profile retrieved", response));
        } catch (RuntimeException e) {
            log.error("Error retrieving KYC profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/me/kyc")
    public ResponseEntity<ApiResponse<KycResponse>> submitKyc(
            @Valid @RequestBody KycRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.saveKycStep(userId, request);
            KycResponse response = mapToKycResponse(profile);
            return ResponseEntity.ok(new ApiResponse<>(true, "KYC submitted successfully", response));
        } catch (RuntimeException e) {
            log.error("Error submitting KYC: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/me/kyc/step/{step}")
    public ResponseEntity<ApiResponse<KycResponse>> saveKycStep(
            @PathVariable Integer step,
            @Valid @RequestBody KycRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            request.setStep(step);
            KycProfile profile = kycService.saveKycStep(userId, request);
            KycResponse response = mapToKycResponse(profile);
            return ResponseEntity.ok(new ApiResponse<>(true, "KYC step saved", response));
        } catch (RuntimeException e) {
            log.error("Error saving KYC step {}: {}", step, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/me/kyc/status")
    public ResponseEntity<ApiResponse<String>> getKycStatus(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);
            String status = profile.getStatus().name();
            return ResponseEntity.ok(new ApiResponse<>(true, "KYC status retrieved", status));
        } catch (RuntimeException e) {
            log.error("Error retrieving KYC status: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    private KycResponse mapToKycResponse(KycProfile profile) {
        if (profile == null) {
            return null;
        }
        return KycResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender() != null ? profile.getGender().name() : null)
                .idType(profile.getIdType())
                .idNumber(profile.getIdNumber())
                .residenceAddress(profile.getResidenceAddress())
                .businessDetails(profile.getBusinessDetails())
                .maritalStatus(profile.getMaritalStatus() != null ? profile.getMaritalStatus().name() : null)
                .numberOfDependents(profile.getNumberOfDependents())
                .status(profile.getStatus().name())
                .rejectionReason(profile.getRejectionReason())
                .completionStep(profile.getCompletionStep())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .approvedAt(profile.getApprovedAt())
                .build();
    }
}
