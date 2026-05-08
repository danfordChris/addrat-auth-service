package com.pesa.service;

import com.pesa.common.exception.BadRequestException;
import com.pesa.dto.KycRequest;
import com.pesa.entity.KycProfile;
import com.pesa.entity.KycProfile.KycStep;
import com.pesa.entity.KycDocument;
import com.pesa.repository.KycProfileRepository;
import com.pesa.repository.KycDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class KycService {

    private static final Logger log = LoggerFactory.getLogger(KycService.class);
    private final KycProfileRepository kycProfileRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private static final String UPLOAD_DIR = "/tmp/pesa-documents";

    public KycService(KycProfileRepository kycProfileRepository, KycDocumentRepository kycDocumentRepository) {
        this.kycProfileRepository = kycProfileRepository;
        this.kycDocumentRepository = kycDocumentRepository;
    }

    public KycProfile getKycProfile(Long userId) {
        return kycProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("KYC profile not found"));
    }

    @Transactional
    public KycProfile saveKycStep(Long userId, KycRequest request) {
        KycProfile profile = getKycProfile(userId);
        KycStep step = request.getStep() != null ? request.getStep() : KycStep.PERSONAL_INFO;

        applyPersonalInfo(profile, request);
        applyEmploymentInfo(profile, request);
        applyFinancialInfo(profile, request);
        profile.setCompletionStep(step);

        if (step == KycStep.APPROVED) {
            profile.setStatus(KycProfile.KycStatus.APPROVED);
            profile.setApprovedAt(java.time.LocalDateTime.now());
        }

        return kycProfileRepository.save(profile);
    }

    @Transactional
    @SuppressWarnings("null")
    public KycDocument uploadDocument(Long kycProfileId, KycDocument.DocumentType documentType, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            KycDocument document = KycDocument.builder()
                    .kycProfileId(kycProfileId)
                    .documentType(documentType)
                    .fileName(fileName)
                    .fileUrl("/documents/" + fileName)
                    .fileSizeBytes(file.getSize())
                    .mimeType(file.getContentType())
                    .build();

            return kycDocumentRepository.save(document);
        } catch (IOException e) {
            log.error("Failed to upload document: {}", e.getMessage());
            throw new RuntimeException("Failed to upload document");
        }
    }

    public List<KycDocument> getDocuments(Long kycProfileId) {
        return kycDocumentRepository.findByKycProfileId(kycProfileId);
    }

    public boolean isKycComplete(Long userId) {
        KycProfile profile = getKycProfile(userId);
        return profile.getStatus() == KycProfile.KycStatus.APPROVED &&
                profile.getCompletionStep() == KycProfile.KycStep.APPROVED;
    }

    private void applyPersonalInfo(KycProfile profile, KycRequest request) {
        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            profile.setGender(parseEnum(request.getGender(), KycProfile.Gender.class, "gender"));
        }
        if (request.getIdType() != null) {
            profile.setIdType(parseEnum(request.getIdType(), KycProfile.IdType.class, "ID type"));
        }
        if (request.getIdNumber() != null) {
            profile.setIdNumber(request.getIdNumber());
        }
        if (request.getResidenceAddress() != null) {
            profile.setResidenceAddress(request.getResidenceAddress());
        }
        if (request.getBusinessDetails() != null) {
            profile.setBusinessDetails(request.getBusinessDetails());
        }
        if (request.getMaritalStatus() != null) {
            profile.setMaritalStatus(
                    parseEnum(request.getMaritalStatus(), KycProfile.MaritalStatus.class, "marital status"));
        }
    }

    private void applyEmploymentInfo(KycProfile profile, KycRequest request) {
        if (request.getEmploymentStatus() != null) {
            profile.setEmploymentStatus(
                    parseEnum(request.getEmploymentStatus(), KycProfile.EmploymentStatus.class, "employment status"));
        }
        if (request.getEmployerName() != null) {
            profile.setEmployerName(request.getEmployerName());
        }
        if (request.getEmployerAddress() != null) {
            profile.setEmployerAddress(request.getEmployerAddress());
        }
        if (request.getTinNumber() != null) {
            profile.setTinNumber(request.getTinNumber());
        }
        if (request.getBusinessName() != null) {
            profile.setBusinessName(request.getBusinessName());
        }
        if (request.getBusinessTinNumber() != null) {
            profile.setBusinessTinNumber(request.getBusinessTinNumber());
        }
        if (request.getBusinessRegistrationNumber() != null) {
            profile.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        }
    }

    private void applyFinancialInfo(KycProfile profile, KycRequest request) {
        if (request.getIncomeRange() != null) {
            profile.setIncomeRange(request.getIncomeRange());
        }
        if (request.getIncomeSource() != null) {
            profile.setIncomeSource(
                    parseEnum(request.getIncomeSource(), KycProfile.IncomeSource.class, "income source"));
        }
        if (request.getLoanAmountRequested() != null) {
            profile.setLoanAmountRequested(request.getLoanAmountRequested());
        }
        if (request.getLoanPurpose() != null) {
            profile.setLoanPurpose(request.getLoanPurpose());
        }
        if (request.getRepaymentPeriodMonths() != null) {
            profile.setRepaymentPeriodMonths(request.getRepaymentPeriodMonths());
        }
        if (request.getNumberOfDependents() != null) {
            profile.setNumberOfDependents(request.getNumberOfDependents());
        }
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumType, String fieldName) {
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid {} value: {}", fieldName, value);
            throw new BadRequestException("Invalid " + fieldName + ": " + value);
        }
    }
}
