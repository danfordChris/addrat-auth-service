package com.pesa.service;

import com.pesa.dto.KycRequest;
import com.pesa.entity.KycProfile;
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
            .orElseThrow(() -> new RuntimeException("KYC profile not found"));
    }

    @Transactional
    public KycProfile saveKycStep(Long userId, KycRequest request) {
        KycProfile profile = getKycProfile(userId);

        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());

        if (request.getGender() != null) {
            try {
                profile.setGender(KycProfile.Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid gender value: {}", request.getGender());
            }
        }

        if (request.getIdType() != null) profile.setIdType(request.getIdType());
        if (request.getIdNumber() != null) profile.setIdNumber(request.getIdNumber());
        if (request.getResidenceAddress() != null) profile.setResidenceAddress(request.getResidenceAddress());
        if (request.getBusinessDetails() != null) profile.setBusinessDetails(request.getBusinessDetails());

        if (request.getMaritalStatus() != null) {
            try {
                profile.setMaritalStatus(KycProfile.MaritalStatus.valueOf(request.getMaritalStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid marital status value: {}", request.getMaritalStatus());
            }
        }

        if (request.getNumberOfDependents() != null) profile.setNumberOfDependents(request.getNumberOfDependents());

        Integer step = request.getStep() != null ? request.getStep() : 0;
        profile.setCompletionStep(step);

        if (step >= 5) {
            profile.setStatus(KycProfile.KycStatus.APPROVED);
            profile.setApprovedAt(java.time.LocalDateTime.now());
        }

        return kycProfileRepository.save(profile);
    }

    @Transactional
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
               profile.getCompletionStep() >= 5;
    }
}
