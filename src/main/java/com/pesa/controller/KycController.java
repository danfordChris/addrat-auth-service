package com.pesa.controller;

import com.pesa.common.api.ApiResponse;
import com.pesa.common.api.ApiResponses;
import com.pesa.dto.KycRequest;
import com.pesa.dto.KycResponse;
import com.pesa.dto.PersonalInfoRequest;
import com.pesa.dto.EmploymentInfoRequest;
import com.pesa.dto.FinancialDetailsRequest;
import com.pesa.dto.KycStatusRequest;
import com.pesa.entity.KycProfile;
import com.pesa.entity.KycProfile.KycStep;
import com.pesa.entity.KycDocument;
import com.pesa.service.KycService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;

@RestController
@RequestMapping("/kyc")
public class KycController {

    private static final Logger log = LoggerFactory.getLogger(KycController.class);
    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    @GetMapping
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

    @PostMapping("/step/{step}")
    public ResponseEntity<ApiResponse<?>> saveKycStep(
            @PathVariable Integer step,
            @Valid @RequestBody KycRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();

            request.setStep(KycStep.fromInteger(step));
            KycProfile profile = kycService.saveKycStep(userId, request);
            KycResponse response = mapToKycResponse(profile);
            return ResponseEntity.ok(ApiResponses.success("KYC step saved", response));
        } catch (RuntimeException e) {
            log.error("Error saving KYC step {}: {}", step, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/documents/{documentType}")
    public ResponseEntity<ApiResponse<?>> uploadDocument(
            @PathVariable String documentType,
            @RequestParam("file") MultipartFile file,
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

    @GetMapping("/documents")
    public ResponseEntity<ApiResponse<?>> getDocuments(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);
            var documents = kycService.getDocuments(profile.getId());
            return ResponseEntity.ok(ApiResponses.success("Documents retrieved", documents));
        } catch (RuntimeException e) {
            log.error("Error retrieving documents: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> getKycStatus(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);
            KycStatusRequest statusResponse = KycStatusRequest.builder()
                    .status(profile.getStatus().name())
                    .completedSteps(Arrays.asList(profile.getCompletionStep().getStepNumber()))
                    .build();
            return ResponseEntity.ok(ApiResponses.success("KYC status", statusResponse));
        } catch (RuntimeException e) {
            log.error("Error retrieving KYC status: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/personal-info")
    public ResponseEntity<ApiResponse<?>> savePersonalInfo(
            @Valid @RequestBody PersonalInfoRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);

            profile.setFullName(request.getFirstName() + " " + (request.getMiddleName() != null ? request.getMiddleName() + " " : "") + request.getLastName());
            profile.setGender(KycProfile.Gender.valueOf(request.getGender().toUpperCase()));
            profile.setDateOfBirth(java.time.LocalDate.parse(request.getDateOfBirth()));
            profile.setMaritalStatus(KycProfile.MaritalStatus.valueOf(request.getMaritalStatus().toUpperCase()));
            profile.setIdNumber(request.getNidaNumber());
            profile.setResidenceAddress(request.getPhysicalAddress());
            profile.setCompletionStep(KycProfile.KycStep.PERSONAL_INFO);

            kycService.saveKycProfile(profile);
            KycResponse response = mapToKycResponse(profile);
            return ResponseEntity.ok(ApiResponses.success("Personal info saved", response));
        } catch (Exception e) {
            log.error("Error saving personal info: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/employment-info")
    public ResponseEntity<ApiResponse<?>> saveEmploymentInfo(
            @Valid @RequestBody EmploymentInfoRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);

            profile.setEmploymentStatus(KycProfile.EmploymentStatus.valueOf(request.getEmploymentStatus().toUpperCase()));
            profile.setEmployerName(request.getEmployerName());
            profile.setEmployerAddress(request.getEmployerAddress());
            profile.setTinNumber(request.getTinNumber());
            profile.setBusinessName(request.getBusinessName());
            profile.setBusinessTinNumber(request.getBusinessTinNumber());
            profile.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
            profile.setNumberOfDependents(request.getNumberOfDependants());
            profile.setCompletionStep(KycProfile.KycStep.EMPLOYMENT_INFO);

            kycService.saveKycProfile(profile);
            KycResponse response = mapToKycResponse(profile);
            return ResponseEntity.ok(ApiResponses.success("Employment info saved", response));
        } catch (Exception e) {
            log.error("Error saving employment info: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/financial-details")
    public ResponseEntity<ApiResponse<?>> saveFinancialDetails(
            @Valid @RequestBody FinancialDetailsRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getDetails();
            KycProfile profile = kycService.getKycProfile(userId);

            profile.setIncomeSource(KycProfile.IncomeSource.valueOf(request.getSourceOfIncome().toUpperCase()));
            profile.setIncomeRange(request.getIncomeRange());
            profile.setCompletionStep(KycProfile.KycStep.FINANCIAL_INFO);

            kycService.saveKycProfile(profile);
            KycResponse response = mapToKycResponse(profile);
            return ResponseEntity.ok(ApiResponses.success("Financial details saved", response));
        } catch (Exception e) {
            log.error("Error saving financial details: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<?>> submitKyc(Authentication authentication) {
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
