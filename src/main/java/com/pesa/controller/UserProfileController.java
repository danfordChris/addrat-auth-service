package com.pesa.controller;

import com.pesa.common.api.ApiResponse;
import com.pesa.common.api.ApiResponses;
import com.pesa.dto.*;
import com.pesa.entity.KycDocument;
import com.pesa.entity.KycProfile;
import com.pesa.entity.User;
import com.pesa.repository.UserRepository;
import com.pesa.service.KycService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/users/me")
public class UserProfileController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);
    private final UserRepository userRepository;
    private final KycService kycService;

    public UserProfileController(UserRepository userRepository, KycService kycService) {
        this.userRepository = userRepository;
        this.kycService = kycService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getProfile(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDto userDto = UserDto.builder()
                    .id(user.getId())
                    .phoneNumber(user.getPhoneNumber())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .kycStatus("PENDING")
                    .creditLimit("0")
                    .build();

            return ResponseEntity.ok(ApiResponses.success("User profile retrieved", userDto));
        } catch (RuntimeException e) {
            log.error("Error retrieving user profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/pin")
    public ResponseEntity<ApiResponse<?>> setPin(
            @Valid @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            String pin = body.get("pin");

            if (pin == null || pin.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponses.error("PIN is required"));
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPin(pin);
            userRepository.save(user);

            return ResponseEntity.ok(ApiResponses.success("PIN set successfully", null));
        } catch (RuntimeException e) {
            log.error("Error setting PIN: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PatchMapping("/device-token")
    public ResponseEntity<ApiResponse<?>> updateDeviceToken(
            @Valid @RequestBody Map<String, String> body,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            String deviceToken = body.get("deviceToken");

            if (deviceToken == null || deviceToken.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponses.error("Device token is required"));
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setDeviceToken(deviceToken);
            userRepository.save(user);

            return ResponseEntity.ok(ApiResponses.success("Device token updated", null));
        } catch (RuntimeException e) {
            log.error("Error updating device token: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @GetMapping("/kyc")
    public ResponseEntity<ApiResponse<?>> getKycProfile(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);
            KycResponse response = mapToKycResponse(profile);
            return ResponseEntity.ok(ApiResponses.success("KYC profile retrieved", response));
        } catch (RuntimeException e) {
            log.error("Error retrieving KYC profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/kyc/step/{step}")
    public ResponseEntity<ApiResponse<?>> saveKycStep(
            @PathVariable Integer step,
            @Valid @RequestBody KycRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();

            request.setStep(KycProfile.KycStep.fromInteger(step));
            KycProfile profile = kycService.saveKycStep(userId, request);
            KycResponse response = mapToKycResponse(profile);
            return ResponseEntity.ok(ApiResponses.success("KYC step saved", response));
        } catch (RuntimeException e) {
            log.error("Error saving KYC step {}: {}", step, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/kyc/documents")
    public ResponseEntity<ApiResponse<?>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "documentType", defaultValue = "SELFIE") String documentType,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);

            KycDocument.DocumentType docType = KycDocument.DocumentType.valueOf(documentType.toUpperCase());
            KycDocument document = kycService.uploadDocument(profile.getId(), docType, file);

            return ResponseEntity.ok(ApiResponses.success("Document uploaded", document));
        } catch (RuntimeException e) {
            log.error("Error uploading document: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/kyc/submit")
    public ResponseEntity<ApiResponse<?>> submitKyc(
            @RequestBody(required = false) Map<String, ?> body,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);
            profile.setStatus(KycProfile.KycStatus.PENDING);
            profile.setCompletionStep(KycProfile.KycStep.APPROVED);
            kycService.saveKycProfile(profile);

            KycStatusRequest statusResponse = KycStatusRequest.builder()
                    .status(profile.getStatus().name())
                    .completedSteps(Arrays.asList(profile.getCompletionStep().getStepNumber()))
                    .build();
            return ResponseEntity.ok(ApiResponses.success("KYC submitted", statusResponse));
        } catch (Exception e) {
            log.error("Error submitting KYC: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
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
                .idType(profile.getIdType() != null ? profile.getIdType().name() : null)
                .idNumber(profile.getIdNumber())
                .residenceAddress(profile.getResidenceAddress())
                .businessDetails(profile.getBusinessDetails())
                .employmentStatus(profile.getEmploymentStatus() != null ? profile.getEmploymentStatus().name() : null)
                .employerName(profile.getEmployerName())
                .employerAddress(profile.getEmployerAddress())
                .tinNumber(profile.getTinNumber())
                .businessName(profile.getBusinessName())
                .businessTinNumber(profile.getBusinessTinNumber())
                .businessRegistrationNumber(profile.getBusinessRegistrationNumber())
                .incomeRange(profile.getIncomeRange())
                .incomeSource(profile.getIncomeSource() != null ? profile.getIncomeSource().name() : null)
                .loanAmountRequested(profile.getLoanAmountRequested())
                .loanPurpose(profile.getLoanPurpose())
                .repaymentPeriodMonths(profile.getRepaymentPeriodMonths())
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
